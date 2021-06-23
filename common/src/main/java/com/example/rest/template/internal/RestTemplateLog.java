package com.example.rest.template.internal;

import com.example.json.JSON;
import com.example.store.log.ILog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestTemplateLog implements ILog {

	private String requestId;

	private int traceNo;

	private String url;

	private String method;

	private int bodyLength;

	private long startTime;

	private String startTimeStr;

	private long endTime;

	private String endTimeStr;

	private long execTime;

	private int responseStatus;

	private String responseBody;

	private String errorMsg;

	@Override
	public String toString() {
		return JSON.toJSONStringDisableHtmlEscaping(this);
	}
}
