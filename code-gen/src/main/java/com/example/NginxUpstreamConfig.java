package com.example;

import java.util.Scanner;

public class NginxUpstreamConfig {
	public static void main(String[] args) {
		try (Scanner scanner = new Scanner(System.in)) {
			int start = 0, end = 0;
			String service = "";
			int index = 0;
			while (scanner.hasNextLine() && index < 3) {
				if (index == 0) {
					start = Integer.parseInt(scanner.nextLine());
				} else if (index == 1) {
					end = Integer.parseInt(scanner.nextLine());
				} else {
					service = scanner.nextLine();
				}
				index++;
			}
			System.out.println("upstream " + service + " {");
			for (int i = start; i < end; i++) {
				System.out.println("\tserver 127.0.0.1:" + i + ";");
			}
			System.out.println("}");
			System.out.println("server {");
			System.out.println("\tlisten 30000;");
			System.out.println("\tserver_name " + service + ".com;");
			System.out.println("\tlocation ~*^.+$ {");
			System.out.println("\t\tproxy_pass http://" + service + ";");
			System.out.println("\t}");
			System.out.println("}");
		}
	}
}
