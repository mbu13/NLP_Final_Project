package Interface;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

public class FilterComboBox<E> extends JComboBox<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FilterComboBox() { 
    } 

    public FilterComboBox(final E items[]){ 
        super(items); 
    } 

    public FilterComboBox(Vector<E> items) { 
        super(items); 
    } 

        public FilterComboBox(ComboBoxModel<E> aModel) { 
        super(aModel); 
    } 

    private boolean layingOut = false; 

    public void doLayout(){ 
        try{ 
            layingOut = true; 
                super.doLayout(); 
        }finally{ 
            layingOut = false; 
        } 
    } 

    public Dimension getSize(){ 
        Dimension dim = super.getSize(); 
        if(!layingOut) 
            dim.width = 100; 
        return dim; 
    } 
}
