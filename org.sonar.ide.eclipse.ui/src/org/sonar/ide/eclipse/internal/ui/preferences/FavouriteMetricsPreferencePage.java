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
package org.sonar.ide.eclipse.internal.ui.preferences;

import com.google.common.collect.Lists;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.sonar.ide.eclipse.core.ISonarMetric;
import org.sonar.ide.eclipse.ui.SonarUiPlugin;

import java.util.List;

public class FavouriteMetricsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  private Table metricsTable;
  private Button removeButton;

  public void init(IWorkbench workbench) {
    setDescription("Manage favourite metrics:");
  }

  @Override
  protected Control createContents(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    layout.numColumns = 2;
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    TableViewer viewer = new TableViewer(composite, SWT.BORDER);
    metricsTable = viewer.getTable();
    metricsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    metricsTable.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event e) {
        updateEnablement();
      }
    });

    removeButton = new Button(composite, SWT.PUSH);
    removeButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
    removeButton.setText("Remove");
    removeButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE).createImage());
    removeButton.setEnabled(false);
    removeButton.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event e) {
        removeFavourite();
      }
    });

    fillTable(SonarUiPlugin.getFavouriteMetricsManager().get());

    return composite;
  }

  private void fillTable(List<ISonarMetric> metrics) {
    metricsTable.removeAll();
    for (ISonarMetric metric : metrics) {
      TableItem item = new TableItem(metricsTable, SWT.NONE);
      item.setData(metric);
      item.setText(metric.getName());
    }
  }

  private void updateEnablement() {
    if (metricsTable.getSelectionCount() > 0) {
      removeButton.setEnabled(true);
    } else {
      removeButton.setEnabled(false);
    }
  }

  private void removeFavourite() {
    int[] selection = metricsTable.getSelectionIndices();
    metricsTable.remove(selection);
  }

  @Override
  public boolean performOk() {
    List<ISonarMetric> metrics = Lists.newArrayList();
    for (TableItem item : metricsTable.getItems()) {
      ISonarMetric metric = (ISonarMetric) item.getData();
      metrics.add(metric);
    }
    SonarUiPlugin.getFavouriteMetricsManager().set(metrics);
    return true;
  }

  @Override
  protected void performDefaults() {
    super.performDefaults();
    SonarUiPlugin.getFavouriteMetricsManager().restoreDefaults();
    fillTable(SonarUiPlugin.getFavouriteMetricsManager().get());
  }
}
