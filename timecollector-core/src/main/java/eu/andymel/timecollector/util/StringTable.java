package eu.andymel.timecollector.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class to add rows of String[] and get a formatted table with toString afterwards.
 * This class takes care of formatting the columns in a way that the widest cell determines the width of the column.
 * 
 * @author andymatic
 */
public class StringTable {

	private static final int DEFAULT_PADDING = 3;
	
	private AvgMaxCalcLong[] columnWidths;
	private List<String[]> rows; 
	
	public StringTable() {
	}
	
	public void sort(Comparator<String[]> comp){
		rows.sort(comp);
	}
	
	public StringTable row(String...columns){
		
		if(columns==null || columns.length==0)return this;
		
		int thisEntrysNumberOfColumns = columns.length;
		
		if(rows==null){
			rows = new ArrayList<>();
			columnWidths = new AvgMaxCalcLong[thisEntrysNumberOfColumns];
			for(int i=0; i<thisEntrysNumberOfColumns; i++){
				columnWidths[i] = AvgMaxCalcLong.createInitializedWithZero();
			}
		}else if(getNumberOfColumns() != thisEntrysNumberOfColumns){
			throw new IllegalArgumentException("This table has "+getNumberOfColumns()+" columns. "
				+ "You tried to add a row with "+thisEntrysNumberOfColumns+" columns!");
		}
	
		rows.add(columns);
		for(int i=0; i<thisEntrysNumberOfColumns; i++){
			if(columns[i]!=null){
				columnWidths[i].add(columns[i].length());	
			}
		}
		
		return this;
	}

	public int[] getColumnWidthsMax(){
		int[] columnWidths = new int[getNumberOfColumns()];
		for(int i=0; i<columnWidths.length; i++){
			columnWidths[i] = (int)this.columnWidths[i].getMax();
		}
		return columnWidths;
	}
	
	public String[][] asArray() {
		if(getNumberOfRows()==0){
			return new String[0][getNumberOfColumns()];
		}
		int nrOfColumns = columnWidths.length;
		String[][] arr = new String[rows.size()][nrOfColumns];
		rows.toArray(arr);
		return arr;
	}

	public int getNumberOfRows() {
		if(rows==null)return 0;
		return rows.size();
	}
	/**
	 * @return the number of columns of the table or -1 if no row has been added
	 */
	public int getNumberOfColumns(){
		if(rows==null || rows.size()==0)return 0;
		return rows.get(0).length;
	}
	

	@Override
	public String toString() {
		return toString(null);
	}
	
	/**
	 * @param offsets the number of spaces to append to the max column width
	 */
	public String toString(int... offsets) {
		if(offsets==null){
			offsets = new int[columnWidths.length];
			Arrays.fill(offsets, DEFAULT_PADDING);
		}else if(offsets.length!=columnWidths.length){
			if(offsets.length==1){
				int o = offsets[0];
				offsets = new int[columnWidths.length];
				Arrays.fill(offsets, o);
			}else{
				throw new IllegalArgumentException("This table has "+getNumberOfColumns()+" columns. "
					+ "You gave "+offsets.length+" offsets!");
			}
		}
		
		
		StringBuilder sbf = new StringBuilder();
		for(int c=0; c<columnWidths.length; c++){
			int offset = Math.max(offsets[c], 0);
			
//			if(offset<0)throw new IllegalArgumentException("The offset with idx "+c+" is "+offset);  maybe someone wants to decrease the column width...test it (TODO)
			int colWidth = (int)columnWidths[c].getMax()+offset;
			sbf.append('%');
			if(colWidth>0){
				sbf.append(colWidth);	
			}
			sbf.append('s');
		}
		String formatString = sbf.toString();
		
		StringBuilder sb = new StringBuilder();
		for(String[] row:rows){
			String txt;
			try{
				txt = String.format(formatString, row);
			}catch(Exception e){
				throw new RuntimeException("Invalid formatString '"+formatString+"'", e);
			}
			sb.append(txt).append('\n');	
		}
		
		return sb.toString();
	}
	
}
