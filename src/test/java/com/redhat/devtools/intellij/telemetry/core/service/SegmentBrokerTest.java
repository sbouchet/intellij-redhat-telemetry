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

import com.redhat.devtools.intellij.telemetry.core.IEventBroker;
import com.segment.analytics.Analytics;
import com.segment.analytics.messages.Message;
import com.segment.analytics.messages.MessageBuilder;
import com.segment.analytics.messages.TrackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static com.redhat.devtools.intellij.telemetry.core.service.Fakes.environment;
import static com.redhat.devtools.intellij.telemetry.core.service.SegmentBroker.PROP_APPLICATION_NAME;
import static com.redhat.devtools.intellij.telemetry.core.service.SegmentBroker.PROP_APPLICATION_VERSION;
import static com.redhat.devtools.intellij.telemetry.core.service.SegmentBroker.PROP_EXTENSION_NAME;
import static com.redhat.devtools.intellij.telemetry.core.service.SegmentBroker.PROP_EXTENSION_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SegmentBrokerTest {

    private static final String EXTENSION_NAME = "com.redhat.devtools.intellij.telemetry";
    private static final String EXTENSION_VERSION = "extension-0.0.1";
    private static final String APPLICATION_NAME = SegmentBrokerTest.class.getSimpleName();
    private static final String APPLICATION_VERSION = "application-1.0.0";
    private static final String ANONYMOUS_ID = "42";
    private static final TrackEvent EVENT_TRACK = new TrackEvent("Testing Telemetry", null);

    private Analytics analytics;
    private Environment environment;
    private SegmentBroker broker;

    @BeforeEach
    public void before() {
        this.analytics = createAnalytics();
        this.environment = environment(EXTENSION_NAME, EXTENSION_VERSION, APPLICATION_NAME, APPLICATION_VERSION);
        this.broker = new SegmentBroker(ANONYMOUS_ID, analytics, environment);
    }

    @Test
    public void send_should_enqueue_track_message() {
        // given
        // when
        broker.send(EVENT_TRACK);
        // then
        verify(analytics).enqueue(isA(TrackMessage.Builder.class));
    }

    @Test
    public void send_should_enqueue_track_message_with_anonymousId() {
        // given
        ArgumentCaptor<MessageBuilder<?,?>> builder = ArgumentCaptor.forClass(MessageBuilder.class);
        // when
        broker.send(EVENT_TRACK);
        // then
        verify(analytics).enqueue(builder.capture());
        Message message = builder.getValue().build();
        assertThat(message.anonymousId()).isEqualTo(ANONYMOUS_ID);
    }

    @Test
    public void send_should_NOT_enqueue_if_no_analytics() {
        // given
        IEventBroker broker = new SegmentBroker(ANONYMOUS_ID, null, environment);
        // when
        broker.send(EVENT_TRACK);
        // then
        verify(analytics, never()).enqueue(any());
    }

    @Test
    public void send_should_enqueue_track_message_with_extension_and_application_names_and_versions() {
        // given
        ArgumentCaptor<MessageBuilder<?,?>> builder = ArgumentCaptor.forClass(MessageBuilder.class);
        // when
        broker.send(EVENT_TRACK);
        // then
        verify(analytics).enqueue(builder.capture());
        Map<String, ?> context = builder.getValue().build().context();
        assertThat(context.get(PROP_EXTENSION_NAME)).isEqualTo(EXTENSION_NAME);
        assertThat(context.get(PROP_EXTENSION_VERSION)).isEqualTo(EXTENSION_VERSION);
        assertThat(context.get(PROP_APPLICATION_NAME)).isEqualTo(APPLICATION_NAME);
        assertThat(context.get(PROP_APPLICATION_VERSION)).isEqualTo(APPLICATION_VERSION);
    }

    @Test
    public void dispose_should_flush_and_shutdown_analytics() {
        // given
        // when
        broker.dispose();
        // then
        verify(analytics).flush();
        verify(analytics).shutdown();
    }

    private Analytics createAnalytics() {
        return mock(Analytics.class);
    }

}
