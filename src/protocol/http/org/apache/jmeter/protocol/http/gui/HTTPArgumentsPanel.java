package org.apache.jmeter.protocol.http.gui;

import java.awt.event.FocusEvent;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.swing.event.ChangeEvent;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.gui.util.PowerTableModel;
import org.apache.jmeter.gui.util.TextAreaTableCellEditor;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.Data;
import org.apache.jmeter.util.JMeterUtils;

/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class HTTPArgumentsPanel extends ArgumentsPanel {
	
	private static final String ENCODED_VALUE = JMeterUtils.getResString("encoded_value");
	private static final String ENCODE_OR_NOT = JMeterUtils.getResString("encode?");
	
	protected void initializeTableModel() {
		tableModel = new PowerTableModel(new String[]{Arguments.COLUMN_NAMES[0],Arguments.COLUMN_NAMES[1],
				ENCODED_VALUE,ENCODE_OR_NOT},
				new Class[]{String.class,String.class,String.class,Boolean.class});
	}
	
	public HTTPArgumentsPanel()
	{
		super(JMeterUtils.getResString("paramtable"));
	}		
	
	/****************************************
	 * !ToDo (Method description)
	 *
	 *@return   !ToDo (Return description)
	 ***************************************/
	public TestElement createTestElement()
	{
		Data model = tableModel.getData();
		Arguments args = new Arguments();
		model.reset();
		while(model.next())
		{
			if(((Boolean)model.getColumnValue(ENCODE_OR_NOT)).booleanValue())
			{
				args.addArgument(new HTTPArgument((String)model.getColumnValue(Arguments.COLUMN_NAMES[0]),
						model.getColumnValue(Arguments.COLUMN_NAMES[1])));
			}
			else
			{
				HTTPArgument arg = new HTTPArgument((String)model.getColumnValue(Arguments.COLUMN_NAMES[0]),
						model.getColumnValue(Arguments.COLUMN_NAMES[1]),true);
				arg.setAlwaysEncode(false);
				args.addArgument(arg);
			}
		}
		this.configureTestElement(args);
		return (TestElement)args.clone();
	}
	
	private void updateEncodedColumn(int row)
	{
		if(((Boolean)tableModel.getValueAt(row,3)).booleanValue())
		{
			tableModel.setValueAt(URLEncoder.encode((String)tableModel.getValueAt(row,0))+"="+
					URLEncoder.encode((String)tableModel.getValueAt(row,1)),row,2);
		}
		else
		{
			tableModel.setValueAt(tableModel.getValueAt(row,0)+"="+
					tableModel.getValueAt(row,1),row,2);
		}
		tableModel.fireTableDataChanged();
	}
	
	/****************************************
	 * !ToDo (Method description)
	 *
	 *@param el  !ToDo (Parameter description)
	 ***************************************/
	public void configure(TestElement el)
	{
		super.configure(el);
		if(el instanceof Arguments)
		{
			tableModel.clearData();
			HTTPArgument.convertArgumentsToHTTP((Arguments)el);
			Iterator iter = ((Arguments)el).getArguments().iterator();
			while(iter.hasNext())
			{
				HTTPArgument arg = (HTTPArgument)iter.next();
				tableModel.addRow(new Object[]{arg.getName(),arg.getValue(),
						arg.getEncodedName()+arg.getMetaData()+arg.getEncodedValue(),
						new Boolean(arg.getAlwaysEncode())});
			}
		}
		checkDeleteStatus();
	}
	
	public void focusLost(FocusEvent e)
	{
		super.focusLost(e);
		for(int x = 0; x < tableModel.getRowCount();x++)
		{
			updateEncodedColumn(x);
		}
	}
	
	public void editingCanceled(ChangeEvent e)
	{
	}
	
	public void editingStopped(ChangeEvent e)
	{
		updateEncodedColumn(((TextAreaTableCellEditor)e.getSource()).getRow());
	}

}
