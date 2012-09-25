package com.kelsos.mbrc;

import com.google.inject.AbstractModule;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class RemoteModule extends AbstractModule
{
	@Override
	public void configure()
	{
		bind(Bus.class).toInstance(new Bus(ThreadEnforcer.ANY,"mbrcbus"));
	}
}
