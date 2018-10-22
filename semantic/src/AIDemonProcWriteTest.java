/*
 AIDemonProcWriteTest.java

*/

import java.util.*;

class AIDemonProcWriteTest extends AIDemonProc {

public
Object eval(
 AIFrameSystem inFrameSystem,
 AIFrame inFrame,
 String inSlotName,
 Iterator inSlotValues,
 Object inOpts )
{
 Object obj = AIFrame.getFirst( inSlotValues );
 inFrame.setSlotValue( inSlotName, "hello " + obj );
 return null;
}

}