/*
 * Sonar Eclipse
 * Copyright (C) 2010-2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.ide.eclipse.internal.ui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.eclipse.core.runtime.SafeRunner;
import org.sonar.ide.eclipse.core.AbstractSafeRunnable;
import org.sonar.ide.eclipse.core.ISonarMetric;
import org.sonar.ide.eclipse.internal.ui.preferences.SonarUiPreferenceInitializer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class FavouriteMetricsManager {

  public interface Listener {
    void updated();
  }

  private final Set<Listener> listeners = Sets.newHashSet();

  private final List<ISonarMetric> metrics = Lists.newArrayList();

  public List<ISonarMetric> get() {
    return Collections.unmodifiableList(metrics);
  }

  public boolean isFavorite(ISonarMetric metric) {
    return metrics.contains(metric);
  }

  public void set(Collection<ISonarMetric> metrics) {
    this.metrics.clear();
    this.metrics.addAll(metrics);
    notifyListeners();
  }

  public void toggle(final ISonarMetric metric) {
    if (metrics.contains(metric)) {
      metrics.remove(metric);
    } else {
      metrics.add(metric);
    }
    notifyListeners();
  }

  private void notifyListeners() {
    for (final Listener listener : listeners) {
      SafeRunner.run(new AbstractSafeRunnable() {
        public void run() throws Exception {
          listener.updated();
        }
      });
    }
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  public void restoreDefaults() {
    set(SonarUiPreferenceInitializer.getDefaultFavouriteMetrics());
  }

}
