/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.redhat.devtools.intellij.telemetry.core.service;

import com.intellij.openapi.util.SystemInfo;

public class Platform {

    Platform() {
        /*
         * distribution not determined yet.
         * A possible impl exists in jbosstools-base/usage:
         * https://github.com/jbosstools/jbosstools-base/blob/master/usage/plugins/org.jboss.tools.usage/src/org/jboss/tools/usage/internal/environment/eclipse/LinuxSystem.java
         */
        this(SystemInfo.OS_NAME, null, SystemInfo.OS_VERSION);
    }

    Platform(String name, String distribution, String version) {
        this.name = name;
        this.distribution = distribution;
        this.version = version;
    }

    private String name;
    private String distribution;
    private String version;

    public String getName() {
        return name;
    }

    public String getDistribution() {
        return distribution;
    }

    public String getVersion() {
        return version;
    }
}
