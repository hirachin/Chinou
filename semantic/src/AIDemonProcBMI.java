/*
 AIDemonProcBMI.java
*/

import java.util.*;

class AIDemonProcBMI extends AIDemonProc 
{
	public Object eval( AIFrameSystem inFrameSystem, AIFrame inFrame, String inSlotName, Iterator inSlotValues, Object inOpts )
	{
		Object height = inFrame.readSlotValue( inFrameSystem, "height_cm", false );
		Object weight = inFrame.readSlotValue( inFrameSystem, "weight_kg", false );

		if ( height instanceof Integer && weight instanceof Double)
		{
			double h_m = ((Integer) height).intValue()/100.0;
			double w = ((Double)weight).doubleValue();
			
			double bmi = w/((h_m*h_m));
			
			return AIFrame.makeEnum( new Double(bmi));
		}
		
		return null;
	}

}