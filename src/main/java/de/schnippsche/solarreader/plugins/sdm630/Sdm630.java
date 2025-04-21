/*
 * Copyright (c) 2024-2025 Stefan Toengi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.schnippsche.solarreader.plugins.sdm630;

import de.schnippsche.solarreader.backend.connection.general.ConnectionFactory;
import de.schnippsche.solarreader.backend.connection.modbus.ModbusConnection;
import de.schnippsche.solarreader.backend.connection.modbus.ModbusConnectionFactory;
import de.schnippsche.solarreader.backend.protocol.KnownProtocol;
import de.schnippsche.solarreader.backend.provider.AbstractModbusProvider;
import de.schnippsche.solarreader.backend.provider.ProviderProperty;
import de.schnippsche.solarreader.backend.provider.SupportedInterface;
import de.schnippsche.solarreader.backend.table.Table;
import de.schnippsche.solarreader.backend.util.ModbusConfigurationBuilder;
import de.schnippsche.solarreader.backend.util.Setting;
import de.schnippsche.solarreader.backend.util.TimeEvent;
import de.schnippsche.solarreader.database.Activity;
import de.schnippsche.solarreader.frontend.ui.HtmlInputType;
import de.schnippsche.solarreader.frontend.ui.HtmlWidth;
import de.schnippsche.solarreader.frontend.ui.UIInputElementBuilder;
import de.schnippsche.solarreader.frontend.ui.UIList;
import de.schnippsche.solarreader.plugin.PluginMetadata;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import org.tinylog.Logger;

/**
 * Represents a specific Modbus provider implementation for SDM630 devices. This class extends
 * {@link AbstractModbusProvider} and initializes the connection using a provided {@link
 * ConnectionFactory}.
 */
@PluginMetadata(
    name = "Sdm630",
    version = "1.0.1",
    author = "Stefan Töngi",
    url = "https://github.com/solarreader-plugins/plugin-Sdm630",
    svgImage = "sdm630.svg",
    supportedInterfaces = {
      SupportedInterface.NAMED_USB,
      SupportedInterface.LISTED_USB,
      SupportedInterface.HF2211
    },
    usedProtocol = KnownProtocol.MODBUS,
    supports = "SGM630")
public class Sdm630 extends AbstractModbusProvider {
  /** Constructs an {@code Sdm630} instance with the default {@link ModbusConnectionFactory}. */
  public Sdm630() {
    this((new ModbusConnectionFactory()));
  }

  /**
   * Constructs an {@code Sdm630} instance using the specified {@link ConnectionFactory}.
   *
   * @param connectionFactory a factory for creating {@link ModbusConnection} instances
   */
  public Sdm630(ConnectionFactory<ModbusConnection> connectionFactory) {
    super(connectionFactory);
    Logger.debug("instantiate {}", this.getClass().getName());
  }

  /**
   * Retrieves the resource bundle for the plugin based on the specified locale.
   *
   * <p>This method overrides the default implementation to return a {@link ResourceBundle} for the
   * plugin using the provided locale.
   *
   * @return The {@link ResourceBundle} for the plugin, localized according to the specified locale.
   */
  @Override
  public ResourceBundle getPluginResourceBundle() {
    return ResourceBundle.getBundle("sdm630", locale);
  }

  @Override
  public Activity getDefaultActivity() {
    return new Activity(TimeEvent.TIME, 0, TimeEvent.TIME, 86399, 20, TimeUnit.SECONDS);
  }

  @Override
  public Optional<UIList> getProviderDialog() {
    UIList uiList = new UIList();
    uiList.addElement(
        new UIInputElementBuilder()
            .withId("id-address")
            .withRequired(true)
            .withType(HtmlInputType.TEXT)
            .withColumnWidth(HtmlWidth.HALF)
            .withLabel(resourceBundle.getString("sdm630.address.text"))
            .withName(Setting.PROVIDER_ADDRESS)
            .withPlaceholder(resourceBundle.getString("sdm630.address.text"))
            .withTooltip(resourceBundle.getString("sdm630.address.tooltip"))
            .withInvalidFeedback(resourceBundle.getString("sdm630.address.error"))
            .build());

    return Optional.of(uiList);
  }

  @Override
  public Optional<List<ProviderProperty>> getSupportedProperties() {
    return getSupportedPropertiesFromFile("sdm630_fields.json");
  }

  @Override
  public Optional<List<Table>> getDefaultTables() {
    return getDefaultTablesFromFile("sdm630_tables.json");
  }

  @Override
  public Setting getDefaultProviderSetting() {
    return new ModbusConfigurationBuilder()
        .withBaudrate(19200)
        .withRtuEncoding()
        .withProviderAddress(1)
        .withBlockSize(16)
        .build();
  }

  @Override
  public String testProviderConnection(Setting testSetting) throws IOException {
    try (ModbusConnection testConnection = connectionFactory.createConnection(testSetting)) {
      testConnection.connect();
      return "";
    } catch (Exception e) {
      Logger.error(e.getMessage());
      throw new IOException(resourceBundle.getString("sdm630.connection.error"));
    }
  }

  @Override
  public void doOnFirstRun() {
    doStandardFirstRun();
  }

  @Override
  public boolean doActivityWork(Map<String, Object> variables)
      throws IOException, InterruptedException {
    try (ModbusConnection modbusConnection = getConnection()) {
      modbusConnection.connect();
      doStandardActivity(modbusConnection, variables);
      return true;
    }
  }
}
