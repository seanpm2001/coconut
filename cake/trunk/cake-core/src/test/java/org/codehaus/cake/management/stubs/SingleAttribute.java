/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.management.stubs;

import org.codehaus.cake.management.annotation.ManagedAttribute;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class SingleAttribute {
    private String string;

    public String getString() {
        return string;
    }

    @ManagedAttribute
    public void setString(String string) {
        this.string = string;
    }
}
