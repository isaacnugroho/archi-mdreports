/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archicontribs.mdreports.commandline;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.util.NLS;

import com.archicontribs.mdreports.markdown.MarkdownReportExporter;
import com.archimatetool.commandline.AbstractCommandLineProvider;
import com.archimatetool.commandline.CommandLineState;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.model.IArchimateModel;

/**
 * Command Line interface for Markdown Reports
 * 
 * Typical usage - (should be all on one line):
 * 
 * Archi -consoleLog -nosplash -application com.archimatetool.commandline.app
 * --loadModel "/pathToModel/model.archimate"
 * --markdown.createReport "/pathToOutputFolder"
 * 
 * @author Phillip Beauvoir
 */
public class MarkdownReportProvider extends AbstractCommandLineProvider {

  static final String PREFIX = Messages.MarkdownReportProvider_0;

  static final String OPTION_CREATE_MARKDOWN_REPORT = "markdown.createReport"; //$NON-NLS-1$

  public MarkdownReportProvider() {
  }

  @Override
  public void run(CommandLine commandLine) throws Exception {
    if (!hasCorrectOptions(commandLine)) {
      return;
    }

    String sOutput = commandLine.getOptionValue(OPTION_CREATE_MARKDOWN_REPORT);
    if (!StringUtils.isSet(sOutput)) {
      logError(Messages.MarkdownReportProvider_1);
      return;
    }

    File folderOutput = new File(sOutput);
    folderOutput.mkdirs();
    if (!folderOutput.exists()) {
      logError(NLS.bind(Messages.MarkdownReportProvider_2, sOutput));
      return;
    }

    IArchimateModel model = CommandLineState.getModel();

    if (model == null) {
      throw new IOException(Messages.MarkdownReportProvider_3);
    }

    logMessage(NLS.bind(Messages.MarkdownReportProvider_4, model.getName(), sOutput));

    MarkdownReportExporter ex = new MarkdownReportExporter(model);
    ex.createReport(folderOutput, "index.markdown", new NullProgressMonitor() { //$NON-NLS-1$

      @Override
      public void subTask(String name) {
        logMessage(name);
      }
    });

    logMessage(Messages.MarkdownReportProvider_5);
  }

  @Override
  protected String getLogPrefix() {
    return PREFIX;
  }

  @Override
  public Options getOptions() {
    Options options = new Options();

    Option option = Option.builder()
        .longOpt(OPTION_CREATE_MARKDOWN_REPORT)
        .hasArg().argName(Messages.MarkdownReportProvider_6)
        .desc(Messages.MarkdownReportProvider_7)
        .build();
    options.addOption(option);

    return options;
  }

  private boolean hasCorrectOptions(CommandLine commandLine) {
    return commandLine.hasOption(OPTION_CREATE_MARKDOWN_REPORT);
  }
}
