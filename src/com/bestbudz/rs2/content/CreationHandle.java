package com.bestbudz.rs2.content;

import com.bestbudz.rs2.entity.stoner.Stoner;

public interface CreationHandle {

  void handle(Stoner stoner, ItemCreation data);
}
