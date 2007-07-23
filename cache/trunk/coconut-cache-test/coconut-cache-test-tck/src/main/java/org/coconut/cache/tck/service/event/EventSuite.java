/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({EventServiceGeneral.class, EventEntryAdded.class,
    EventEntryGet.class, EventEntryRemoved.class, EventEntryUpdated.class,
    EventCacheCleared.class})
public class EventSuite {
}
