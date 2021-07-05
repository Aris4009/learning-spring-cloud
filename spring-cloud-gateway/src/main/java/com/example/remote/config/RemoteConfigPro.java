package com.example.remote.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoteConfigPro {

	private String type;

	private String path;

	private String serverAddr;

	private String namespace;

	private String dataId;

	private String group;
}
