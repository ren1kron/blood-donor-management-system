package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.application.command.ProcessReportRequestCommand;
import ifmo.se.coursach_back.report.application.result.ReportRequestDetailsResult;

/**
 * Use case interface for processing a report request and generating the report.
 */
public interface ProcessReportRequestUseCase {
    /**
     * Process a report request and generate the report content.
     * @param command command containing admin ID, request ID, and optional payload
     * @return full details of the processed report
     */
    ReportRequestDetailsResult execute(ProcessReportRequestCommand command);
}
