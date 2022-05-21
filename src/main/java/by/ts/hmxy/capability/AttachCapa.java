package by.ts.hmxy.capability;

import net.minecraftforge.event.AttachCapabilitiesEvent;

public interface AttachCapa {
	<T> void onAttach(AttachCapabilitiesEvent<T> event);	
}
