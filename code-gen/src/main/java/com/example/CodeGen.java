package com.example;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.beetl.core.GroupTemplate;
import org.beetl.core.ReThrowConsoleErrorHandler;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.sql.core.*;
import org.beetl.sql.gen.BaseProject;
import org.beetl.sql.gen.Entity;
import org.beetl.sql.gen.SourceBuilder;
import org.beetl.sql.gen.SourceConfig;
import org.beetl.sql.gen.simple.BaseTemplateSourceBuilder;
import org.beetl.sql.gen.simple.EntitySourceBuilder;
import org.beetl.sql.gen.simple.MDSourceBuilder;
import org.beetl.sql.gen.simple.SimpleMavenProject;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 自动生成orm
 */
public class CodeGen {

	public static void main(String[] args) {
		try {
			List<String> tables = new ArrayList<>();
			System.out.println("输入表名，回车分割");
			Scanner scanner = new Scanner(System.in);
			while (scanner.hasNextLine()) {
				String table = scanner.nextLine();
				if (!StrUtil.isEmptyIfStr(table)) {
					tables.add(table);
				} else {
					break;
				}
			}
			scanner.close();
			if (CollUtil.isEmpty(tables)) {
				return;
			}
			HikariConfig hikariConfig = hikariConfig();
			SQLManager sqlManager = sqlManager(hikariConfig);
			initGroupTemplate();
			genCode(sqlManager, tables);
			sqlManager.getDs().getMetaData().close();
			System.out.println(Arrays.toString(tables.toArray(new String[0])));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static HikariConfig hikariConfig() {
		Props props = new Props("db.properties");
		return new HikariConfig(props);
	}

	public static SQLManager sqlManager(HikariConfig hikariConfig) {
		HikariDataSource dataSource = new HikariDataSource(hikariConfig);
		ConnectionSource connectionSource = ConnectionSourceHelper.getSingle(dataSource);
		SQLManagerBuilder sqlManagerBuilder = new SQLManagerBuilder(connectionSource);
		sqlManagerBuilder.setNc(new UnderlinedNameConversion());
		return sqlManagerBuilder.build();
	}

	public static void initGroupTemplate() {
		GroupTemplate groupTemplate = BaseTemplateSourceBuilder.getGroupTemplate();
		String root = System.getProperty("user.dir");
		String templatePath = root + "/code-gen/src/main/resources/templates/";
		FileResourceLoader resourceLoader = new FileResourceLoader(templatePath);
		groupTemplate.setResourceLoader(resourceLoader);
	}

	public static void genCode(SQLManager sqlManager, List<String> tables) {
		List<SourceBuilder> sourceBuilder = new ArrayList<>();
		SourceBuilder entityBuilder = new EntitySourceBuilder();
		SourceBuilder mapperBuilder = new CustomMapperSourceBuilder("dao");
		SourceBuilder mdBuilder = new MDSourceBuilder();

		sourceBuilder.add(entityBuilder);
		sourceBuilder.add(mapperBuilder);
		sourceBuilder.add(mdBuilder);

		SourceConfig config = new SourceConfig(sqlManager, sourceBuilder);
		// 如果有错误，抛出异常而不是继续运行1
		EntitySourceBuilder.getGroupTemplate().setErrorHandler(new ReThrowConsoleErrorHandler());

		CustomProject project = new CustomProject("com.example");
		project.setRoot(System.getProperty("user.dir") + "/services");

		tables.forEach(table -> config.gen(table, project));
	}

	static class CustomProject extends SimpleMavenProject {
		public CustomProject() {
			super();
		}
		public CustomProject(String basePackage) {
			super(basePackage);
		}
	}

	static class CustomMapperSourceBuilder extends BaseTemplateSourceBuilder {

		/**
		 * 指定模板的路径
		 */
		public static String mapperPath = "mapper.btl";

		public CustomMapperSourceBuilder(String name) {
			super(name);
		}

		// Override
		@Override
		public void generate(BaseProject project, SourceConfig config, Entity entity) {

			Template template = groupTemplate.getTemplate(mapperPath);
			String mapperClass = entity.getName() + "Dao";
			template.binding("className", mapperClass);
			template.binding("package", project.getBasePackage(this.name));
			template.binding("entityClass", entity.getName());
			// 得到生成的entity的包
			String entityPkg = project.getBasePackage("entity");
			String mapperHead = entityPkg + ".*";
			template.binding("imports", Arrays.asList(mapperHead));
			Writer writer = project.getWriterByName(this.name, entity.getName() + "Dao.java");
			template.renderTo(writer);

		}
	}
}
