package backistics;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

import backistics.pageparser.BackyardPage;

class StatAggregator implements Writable
{
	int numReports;
	int numManagers;
	
	StatAggregator()
	{
		numReports = 0;
		numManagers = 0;
	}

	StatAggregator(BackyardPage page)
	{
		numReports = page.directReports.size();
		numManagers = 1;
	}

	StatAggregator(StatAggregator sa)
	{
		numReports += sa.numReports;
		numManagers += sa.numManagers;
	}
	
	public void add(StatAggregator toAdd)
	{
		numReports += toAdd.numReports;
		numManagers += toAdd.numManagers;
	}
	
	@Override
	public void readFields(DataInput dataIn) throws IOException 
	{	
		numReports = dataIn.readInt();
		numManagers = dataIn.readInt();
	}
	
	@Override
	public void write(DataOutput dataOut) throws IOException 
	{
		dataOut.writeInt(numReports);
		dataOut.writeInt(numManagers);
	}
}