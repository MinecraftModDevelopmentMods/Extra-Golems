package com.mcmoddev.golems.proxies;

import net.minecraftforge.event.AddReloadListenerEvent;

public class ServerProxy extends CommonProxy {
  
  @Override
  public void addReloadListeners(AddReloadListenerEvent event) {
    event.addListener(GOLEM_CONTAINERS);
  }
}
