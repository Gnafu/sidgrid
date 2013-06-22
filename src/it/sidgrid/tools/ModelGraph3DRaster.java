/*Copyright (C) 2013  SID&GRID Project

Regione Toscana
Universita' degli Studi di Firenze - Dept. of Mathematics and Computer Science
Scuola Superiore S.Anna
CNR-ISTI

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.*/

/**
 * @author 
 * Claudio Schifani
 * Lorenzo Pini
 * Iacopo Borsi
 * Rudy Rossetto
 */
package it.sidgrid.tools;

import it.sidgrid.utils.Utils;
import visad.DataReferenceImpl;
import visad.Display;
import visad.DisplayImpl;
import visad.DisplayRenderer;
import visad.FlatField;
import visad.FunctionType;
import visad.GraphicsModeControl;
import visad.Linear2DSet;
import visad.LocalDisplay;
import visad.MouseBehavior;
import visad.ProjectionControl;
import visad.RealTupleType;
import visad.RealType;
import visad.ScalarMap;
import visad.Set;
import visad.VisADException;
import visad.java3d.*;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import es.unex.sextante.gvsig.core.gvRasterLayer;
import es.unex.sextante.math.simpleStats.SimpleStats;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.raster.dataset.FileNotOpenException;
import org.gvsig.raster.dataset.IRasterDataSource;
import org.gvsig.raster.dataset.InvalidSetViewException;
import org.gvsig.raster.dataset.io.RasterDriverException;

public class ModelGraph3DRaster extends javax.swing.JPanel implements IWindow{
	 private WindowInfo viewInfo = null;
	 private JComboBox comboModelDocument;
	 private MapControl mapCtrl;
	 private RealType longitude, latitude;
	 private RealType altitude;
	 private RealTupleType domain_tuple;
	 private FunctionType func_domain_alt;
	 private Set domain_set;
	 private DisplayImpl display;
	 private ScalarMap latMap, lonMap;
	 private ScalarMap altMap, altRGBMap;
	 private final Action action = new SwingAction();
	
	
	    /** The default mouse function map */
	    public static final int[][][] defaultMouseFunctions =
	        EventMap.IDV_MOUSE_FUNCTIONS;

	    /** The default mouse  wheel function map */
	    private int[][] wheelEventMap = EventMap.IDV_WHEEL_FUNCTIONS;
	    private JCheckBox chckbxRange;
	    private JLabel lblMax;
	    private JTextField textMax;
	    private JTextField textMin;
	    private JLabel lblMin;


	    /**
	     * Set the mapping between mouse wheel event and function
	     *
	     * @param map The mapping
	     * 
	     */
	    public void setWheelEventMap(int[][] map) {
	        wheelEventMap = map;
	    }

	    
	public ModelGraph3DRaster(MapControl mc) {
		this.mapCtrl = mc;
		setLayout(null);
		ComboBoxModel ModelLayerModel = new DefaultComboBoxModel(Utils.getRasterLayers(mc));
		comboModelDocument = new JComboBox();
		comboModelDocument.setBounds(6, 57, 158, 27);
		comboModelDocument.setModel(ModelLayerModel);
		add(comboModelDocument);
		
		JButton btnView = new JButton("View");
		btnView.setAction(action);
		btnView.setBounds(186, 56, 117, 29);
		add(btnView);
		
		JLabel lblGraphdRender = new JLabel(PluginServices.getText(this,"Graph_3D"));
		lblGraphdRender.setHorizontalAlignment(SwingConstants.CENTER);
		lblGraphdRender.setBounds(10, 6, 293, 16);
		add(lblGraphdRender);
		add(getChckbxRange());
		add(getLblMax());
		add(getTextMax());
		add(getTextMin());
		add(getLblMin());
	}
	
	@Override
	
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "View_3D_model_raster"));
			viewInfo.setHeight(235);
			viewInfo.setWidth(350);
		}
		return viewInfo;
	}
	
	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
	
	
	public class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "View");
		}
		@Override
		public void actionPerformed(ActionEvent e) {							    
		    try {
		    	/*recupero il documento del modello*/
		    	String progetto = (String) comboModelDocument.getSelectedItem();
				//ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
				//HydrologicalModel Modeldoc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
		    	
				/*recupero eventuali limiti all'interfaccia*/
				boolean limit = getChckbxRange().isSelected();
				double zmin = -5000, zmax = 10000;
				if(limit)
				{
					try{
						zmin = Double.parseDouble(getTextMin().getText());
						zmax = Double.parseDouble(getTextMax().getText());
					}catch (NumberFormatException e1) {
						// if got wrong values, no limit is set
						limit = false;
					}
				}
				
				/*definisco i parametri da plottare*/
		    	latitude = RealType.getRealType("latitude", null, null);
			    longitude = RealType.getRealType("longitude", null, null);
				domain_tuple = new RealTupleType(latitude, longitude);
				altitude = RealType.getRealType("quota");
			    func_domain_alt = new FunctionType( domain_tuple, altitude);
			    
			    /*imposto il primo layer del modello */
			    FLayers layers = mapCtrl.getMapContext().getLayers();	
			    FLyrRasterSE griglia = (FLyrRasterSE)layers.getLayer(progetto);
				gvRasterLayer layerGriglia = new gvRasterLayer();
				layerGriglia.create(griglia);
				
				layerGriglia.setFullExtent();
				IRasterDataSource dataset = griglia.getDataSource();
				
				/*Recupero l'array con tutti i layer del modello*/
				//final ArrayList<gvVectorLayer> Modelslayers = Modeldoc.getLayers();
				
				/*definisco le dimensioni di riga e colonna della griglia del modello*/
				/*
				IFeatureIterator iter = layerGriglia..iterator();
				
				final int nrow = layerGriglia.getFieldIndexByName("ROW");
				final int ncol = layerGriglia.getFieldIndexByName("COL");
				final int ntop = layerGriglia.getFieldIndexByName("TOP");
				final int nbottom = layerGriglia.getFieldIndexByName("BOTTOM");
				
				final SimpleStats statsx = new SimpleStats();
				final SimpleStats statsy = new SimpleStats();
				*/
				final SimpleStats statsz = new SimpleStats();
				/*
				while (iter.hasNext()) {
					final IFeature feature = iter.next();					
					int xmax = Integer.parseInt(feature.getRecord().getValue(nrow).toString());
					int ymax = Integer.parseInt(feature.getRecord().getValue(ncol).toString());
					double ttop = Double.parseDouble(feature.getRecord().getValue(ntop).toString());
					double tbottom = Double.parseDouble(feature.getRecord().getValue(nbottom).toString());
						statsx.addValue(xmax);
						statsy.addValue(ymax);
						statsz.addValue(ttop);
						statsz.addValue(tbottom);
					}				
				iter.close();
				*/
				
				for(int x=0; x<layerGriglia.getLayerGridExtent().getNX(); x++)
					for(int y=0; y<layerGriglia.getLayerGridExtent().getNY(); y++)
					{
						try {
							statsz.addValue(Double.parseDouble(dataset.getData(x, y, 0).toString()));
						} catch (NumberFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InvalidSetViewException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FileNotOpenException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (RasterDriverException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				
				// LE STATISTICHE NON SI POSSONO PRENDERE A RIFERIMENTO PER LA DIMENSIONE, SIAMO SU RASTER!
				//int iROW = (int) statsx.getMax();
				//int jCOL =  (int) statsy.getMax();
				int iROW = layerGriglia.getLayerGridExtent().getNY();
				int jCOL =  layerGriglia.getLayerGridExtent().getNX();
				//System.out.println("jROW: "+iROW+" jCOL:"+jCOL);
				/*imposto il dominio del plot*/
				domain_set = new Linear2DSet(domain_tuple, iROW, 0.0, iROW,
				          0.0,  jCOL, jCOL);
				
				/*creo il display*/
				display = new DisplayImplJ3D("display");			    
			    GraphicsModeControl dispGMC = display.getGraphicsModeControl();
			    dispGMC.setScaleEnable(true);
			    dispGMC.setProjectionPolicy(DisplayImplJ3D.PERSPECTIVE_PROJECTION);
			    latMap = new ScalarMap( latitude,    Display.YAxis );
			    lonMap = new ScalarMap( longitude, Display.XAxis );
			    altRGBMap = new ScalarMap( altitude,  Display.RGB );			    
			    altMap = new ScalarMap( altitude,  Display.ZAxis );
			    //altMap.setRange(-50.0f, 100.0f);
			    if(limit)
			    {
			    	double view_zmin=(zmin>statsz.getMin()?zmin:statsz.getMin());
			    	double view_zmax=(zmax<statsz.getMax()?zmax:statsz.getMax());
/*			    	RangeControl zRangeControl = (RangeControl) altRGBMap.getControl();
			    	double[] zRange = { zmin, zmax };
			    	zRangeControl.setRange(zRange);
			    	System.out.println("zmax= :"+zmax+" zmin:"+zmin);
*/			    	altMap.setRange(view_zmin, view_zmax);
			    	altRGBMap.setRange(view_zmin, view_zmax);
			    	//altRGBMap.setRange(zmin, zmax);
			    }else{
			    	altMap.setRange(statsz.getMin()-50, statsz.getMax()+10);
			    	altRGBMap.setRange(statsz.getMin()-50, statsz.getMax()+10);
			    }
			    display.addMap( latMap );
			    display.addMap( lonMap );
			    display.addMap( altMap );
			    display.addMap( altRGBMap );
			    
			    // RGB ScalarMap and a user-defined color table
//			    ColorControl colCont = (ColorControl) altRGBMap.getControl();
//			    colCont.initGreyWedge();
			    
			    ProjectionControl projCont = display.getProjectionControl();
			    double[] aspect = new double[]{1,0.66,0.33};
			    projCont.setAspect( aspect );
			    
			    DisplayRenderer dRenderer = display.getDisplayRenderer();
			    dRenderer.setBackgroundColor(Color.white);
			    dRenderer.setBoxColor(Color.gray);
			    
			    float[] r = colorToFloats(Color.black);
			    latMap.setScaleColor(r);
			    lonMap.setScaleColor(r);
			    altMap.setScaleColor(r);
			    
			    
			    DataReferenceImpl data_ref = new DataReferenceImpl(layerGriglia.getName());
			    data_ref.setData(createDataset(layerGriglia, dataset, limit, zmin, zmax) );			    
			    display.addReference(data_ref);
			    /*			    
			    DataReferenceImpl data_refBott = new DataReferenceImpl(layerGriglia.getName());
			    data_refBott.setData(createDataset(layerGriglia, jCOL, iROW, "BOTTOM", limit, zmin, zmax) );		    
			    display.addReference( data_refBott);



			    for (int i = 1; i<Modelslayers.size(); i++){
			    	gvVectorLayer layer = Modelslayers.get(i);		    	
			    	DataReferenceImpl data_refModelLayer = new DataReferenceImpl(layer.getName());
			    	data_refModelLayer.setData( createDataset(layer, jCOL, iROW, "BOTTOM", limit, zmin, zmax) );
			    	display.addReference( data_refModelLayer);
			    }
			    */
			    PluginServices.getMDIManager().closeWindow(ModelGraph3DRaster.this);
			    JFrame jframe = new JFrame("3D Graph");
			    jframe.setSize(500, 300);		
			    Component displayComp = display.getComponent();
			    displayComp.addMouseWheelListener(
		                new java.awt.event.MouseWheelListener() {
		                @Override
						public void mouseWheelMoved(
		                        java.awt.event.MouseWheelEvent e1) {
		                    handleMouseWheelMoved(e1);
		                }
		            });
			    
				jframe.getContentPane().add(displayComp);
				jframe.setVisible(true);
			    
			} catch (VisADException e1) {
				e1.printStackTrace();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		    
		}
	}
	
	public FlatField createDataset(gvRasterLayer layerGriglia, IRasterDataSource dataset, boolean limit, double zmin, double zmax)
			throws VisADException, RemoteException {
		
		int NCOLS = layerGriglia.getLayerGridExtent().getNX();
		int NROWS = layerGriglia.getLayerGridExtent().getNY();
	    double[] quota = new double[NROWS*NCOLS];
	  	    
		int i = 0;
		Double h;
		try{
		for(int y=0; y<NROWS; y++)
			for(int x=0; x<NCOLS; x++)
			{
				if(limit){
					h = Double.parseDouble(dataset.getData(x, y, 0).toString());
					quota[i] = (h>zmax)?(i==0?zmax:quota[i-1]):(h<zmin)?(i==0?zmin:quota[i-1]):h;
					
				}else
				{
					h = Double.parseDouble(dataset.getData(x, y, 0).toString());
					quota[i] = h;
				}
				i++;
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidSetViewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RasterDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    /* // DEBUG
		System.out.println("Quota");
		for(int g=0; g<quota.length; g++)
			System.out.println(g+" : "+quota[g]);
		*/
	    double[][] alt_samples = new double[NROWS][NCOLS];
	    
		int index = 0;
		
	    for(int r = 0; r < NROWS; r++)
	      for(int c = 0; c < NCOLS; c++){
		      // set altitude
	    	  alt_samples[r][ c ] = quota[index];
		      // increment index
		      index++;
	      }
	    /* // DEBUG
	    int gi=0;
	    System.out.println("alt_samples");
		for(int gr=0; gr<alt_samples.length; gr++)
			for(int gc=0; gc<alt_samples[0].length; gc++)
				System.out.println(gi+++"["+gr+"]["+gc+"] : "+alt_samples[gr][gc]);
	    */
		double[][] flat_samples = new double[1][NCOLS * NROWS];
		
		int index2 = 0;

	    for(int c = 0; c < NCOLS; c++)
		      for(int r = 0; r < NROWS; r++){
		      // set altitude
		      flat_samples[0][index2] = alt_samples[r][c];
		      // increment index
		      index2++;
	      }
	    /* // DEBUG
		System.out.println("flat_samples");
		for(int g=0; g<flat_samples[0].length; g++)
			System.out.println(g+" : "+flat_samples[0][g]);
		*/
	    FlatField vals_ff = new FlatField( func_domain_alt, domain_set);	    
	    vals_ff.setSamples( flat_samples , false );
		return vals_ff;
	}
	
	  private float[] colorToFloats(Color c){

		    float[] rgb = new float[]{0.5f,0.5f,0.5f};  //init with gray
		    if(c != null){
		      rgb[0] = c.getRed()/255.0f;
		      rgb[1] = c.getGreen()/255.0f;
		      rgb[2] = c.getBlue()/255.0f;

		    }

		    return rgb;
		  }
	  
	    /**
	     * Handle when the mouse scroll wheel has been moved
	     *
	     * @param e event
	     */
	    protected void handleMouseWheelMoved(java.awt.event.MouseWheelEvent e) {

	        int    rot     = e.getWheelRotation();
	        double degrees = 2.0;
	        int    control = e.isControlDown()
	                         ? 1
	                         : 0;
	        int    shift   = e.isShiftDown()
	                         ? 1
	                         : 0;
	        int    func    = wheelEventMap[control][shift];
	        if (func == EventMap.WHEEL_ROTATEZ) {
	            if (rot < 0) {
	                rotateZ(degrees);
	            } else {
	                rotateZ(-degrees);
	            }
	        } else if (func == EventMap.WHEEL_ROTATEX) {
	            if (rot < 0) {
	                rotateX(degrees);
	            } else {
	                rotateX(-degrees);
	            }
	        } else if (func == EventMap.WHEEL_ROTATEY) {
	            if (rot < 0) {
	                rotateY(degrees);
	            } else {
	                rotateY(-degrees);
	            }
	        } else if (func == EventMap.WHEEL_ZOOMIN) {
	            if (rot < 0) {
	                zoom(0.9);
	            } else {
	                zoom(1.1);
	            }
	        } else if (func == EventMap.WHEEL_ZOOMOUT) {
	            if (rot < 0) {
	                zoom(1.1);
	            } else {
	                zoom(0.9);
	            }

	        }
	    }
	    /**
	     * Zoom in on the display
	     *
	     * @param  factor  zoom factor
	     *                 ( > 1 = zoom in, 1 > zoom > 0 =  zoom out).  using
	     *                 2.0 and .5 seems to work well.
	     */
	    public void zoom(double factor) {
	        zoom(factor, factor, factor);
	    }




	    /**
	     * Zoom in on the display
	     *
	     * @param  xfactor  x zoom factor
	     * @param  yfactor  y zoom factor
	     * @param  zfactor  z zoom factor
	     *
	     * ( > 1 = zoom in, 1 > zoom > 0 =  zoom out).  using
	     * 2.0 and .5 seems to work well.
	     */
	    public void zoom(double xfactor, double yfactor, double zfactor) {
	        double[] scaleMatrix = getMouseBehavior().make_matrix(0.0, 0.0, 0.0,
	                                   xfactor, yfactor, zfactor, 0.0, 0.0, 0.0);
	        double[] currentMatrix = getProjectionMatrix();
	        scaleMatrix = getMouseBehavior().multiply_matrix(scaleMatrix,
	                currentMatrix);

	        try {
	            setProjectionMatrix(scaleMatrix);
	            //      setProjectionMatrix(xscaleMatrix);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	    }
	    /**
	     * Gets the current display projection.  The object returned from {@link
	     * visad.ProjectionControl#getMatrix()} is returned.
	     *
	     * @return                       The current display projection.
	     */
	    public double[] getProjectionMatrix() {
	        return display.getProjectionControl().getMatrix();
	    }

	    /**
	     * _more_
	     *
	     * @return _more_
	     */
	    public double getScale() {
	        double[] currentMatrix = getProjectionMatrix();
	        double[] trans         = { 0.0, 0.0, 0.0 };
	        double[] rot           = { 0.0, 0.0, 0.0 };
	        double[] scale         = { 0.0, 0.0, 0.0 };
	        getMouseBehavior().instance_unmake_matrix(rot, scale, trans,
	                currentMatrix);

	        return scale[0];
	    }

	    /**
	     * _more_
	     *
	     * @return _more_
	     */
	    public double[] getRotation() {
	        double[] currentMatrix = getProjectionMatrix();
	        double[] trans         = { 0.0, 0.0, 0.0 };
	        double[] rot           = { 0.0, 0.0, 0.0 };
	        double[] scale         = { 0.0, 0.0, 0.0 };
	        getMouseBehavior().instance_unmake_matrix(rot, scale, trans,
	                currentMatrix);

	        return rot;
	    }


	    /**
	     * Sets the current display projection.  The argument is passed, unaltered,
	     * to {@link visad.ProjectionControl#setMatrix(double[])}.
	     *
	     * @param newMatrix              The new projection matrix.
	     * @throws VisADException        if a VisAD failure occurs.
	     * @throws RemoteException       if a Java RMI failure occurs.
	     */
	    public void setProjectionMatrix(double[] newMatrix)
	            throws VisADException, RemoteException {
	        //        System.err.print ("DisplayMaster.setProjectionMatrix ");
	        //        for(int i=0;i<newMatrix.length;i++) 
	        //            System.err.print(" " + newMatrix[i]);
	        //        System.err.println(" ");
	        display.getProjectionControl().setMatrix(newMatrix);
	    }

	    /**
	     * Returns the saved projection Matrix.  The object returned from {@link
	     * visad.ProjectionControl#getSavedProjectionMatrix()} is returned.
	     *
	     * @return                       The saved projection matrix.
	     */
	    public double[] getSavedProjectionMatrix() {
	        return display.getProjectionControl().getSavedProjectionMatrix();
	    }

	    /**
	     * Restores to projection at time of last <code>saveProjection()</code>
	     * call -- if one was made -- or to initial projection otherwise.
	     *
	     * @see #saveProjection()
	     * @throws VisADException   VisAD failure.
	     * @throws RemoteException  Java RMI failure.
	     */
	    public void resetProjection() throws VisADException, RemoteException {
	        display.getProjectionControl().resetProjection();
	    }

	    /**
	     * Get the current mouse behavior
	     *
	     * @return mouse behavior
	     */
	    public MouseBehavior getMouseBehavior() {
	        return getDisplay().getDisplayRenderer().getMouseBehavior();
	    }


	    /**
	     * rotate some angle
	     *
	     * @param  angle rotate angle
	     */
	    public void rotateX(double angle) {
	        rotate(angle, 0.0, 0.0);
	    }


	    /**
	     * rotate some angle
	     *
	     * @param  angle rotate angle
	     */
	    public void rotateY(double angle) {
	        rotate(0.0, angle, 0.0);
	    }



	    /**
	     * rotate some angle
	     *
	     * @param  angle rotate angle
	     */
	    public void rotateZ(double angle) {
	        rotate(0.0, 0.0, angle);
	    }
	    /**
	     * rotate some angle
	     *
	     * @param  anglex rotate angle
	     * @param  angley rotate angle
	     * @param  anglez rotate angle
	     */
	    public void rotate(double anglex, double angley, double anglez) {
	        double[] t1 = getMouseBehavior().make_matrix(anglex, angley, anglez,
	                          1.0, 0.0, 0.0, 0.0);
	        double[] currentMatrix = getProjectionMatrix();
	        t1 = getMouseBehavior().multiply_matrix(t1, currentMatrix);

	        try {
	            setProjectionMatrix(t1);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	    }

	    /**
	     * Returns the associated VisAD display.
	     * @return          The VisAD display.
	     */
	    public final LocalDisplay getDisplay() {
	        return display;
	    }
	private JCheckBox getChckbxRange() {
		if (chckbxRange == null) {
			chckbxRange = new JCheckBox("Limit_vertical_range");
			chckbxRange.setBounds(6, 110, 158, 23);
			chckbxRange.setSelected(true);
		}
		return chckbxRange;
	}
	private JLabel getLblMax() {
		if (lblMax == null) {
			lblMax = new JLabel("Max");
			lblMax.setHorizontalAlignment(SwingConstants.CENTER);
			lblMax.setBounds(170, 114, 46, 14);
		}
		return lblMax;
	}
	private JTextField getTextMax() {
		if (textMax == null) {
			textMax = new JTextField();
			textMax.setBounds(217, 111, 86, 20);
			textMax.setColumns(10);
			textMax.setText("10000");
		}
		return textMax;
	}
	private JTextField getTextMin() {
		if (textMin == null) {
			textMin = new JTextField();
			textMin.setColumns(10);
			textMin.setBounds(217, 139, 86, 20);
			textMin.setText("-5000");
		}
		return textMin;
	}
	private JLabel getLblMin() {
		if (lblMin == null) {
			lblMin = new JLabel("Min");
			lblMin.setHorizontalAlignment(SwingConstants.CENTER);
			lblMin.setBounds(170, 142, 46, 14);
		}
		return lblMin;
	}
}
