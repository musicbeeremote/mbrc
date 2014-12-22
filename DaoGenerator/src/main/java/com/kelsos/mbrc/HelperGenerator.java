package com.kelsos.mbrc;

import de.greenrobot.daogenerator.ContentProvider;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelperGenerator {

	public static final int INCREASE = 3;
	private Template templateHelper;
	private Template templateContentProvider;
	private ContentProvider mProvider;
	private int id;

	public HelperGenerator() throws IOException {
		Configuration config = new Configuration();
		config.setClassForTemplateLoading(this.getClass(), "/");
		config.setObjectWrapper(new DefaultObjectWrapper());

		templateHelper = config.getTemplate("contract.java.ftl");
		templateContentProvider = config.getTemplate("content-provider.java.ftl");
		id = 0;
	}

	public void generateAll(Schema schema, String outDir) {
		long start = System.currentTimeMillis();
		List<Entity> entities = schema.getEntities();

		File outDirFile = null;
		try {
			outDirFile = toFileForceExists(outDir);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mProvider = new ContentProvider(schema, schema.getEntities());
		mProvider.init2ndPass();
		mProvider.setClassName("LibraryProvider");

		for (Entity entity : entities) {
			generateHelpers(schema, entity, outDirFile);
		}

		generateContentProvider(schema, outDirFile);
		long time = System.currentTimeMillis() - start;
		System.out.println("Processed " + entities.size() + " entities in " + time + "ms");
	}

	private void generateHelpers(Schema schema, Entity entity, File outDirFile) {
		Map<String, Object> root = new HashMap<>();
		root.put("schema", schema);
		root.put("entity", entity);
		root.put("contentProvider", mProvider);
		root.put("id", id);
		id  += INCREASE;
		generate(entity.getClassName() + "Helper", outDirFile, root, templateHelper, entity.getJavaPackage());
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void generate(String className, File outDirFile, Map<String, Object> root, Template template, String javaPackage) {
		try {
			File file = toJavaFilename(outDirFile, javaPackage, className);
			file.getParentFile().mkdirs();
			try (Writer writer = new FileWriter(file)) {
				template.process(root, writer);
				writer.flush();
				System.out.println("Written " + file.getCanonicalPath());
			} catch (TemplateException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void generateContentProvider(Schema schema, File outDirFile) {
		Map<String, Object> root = new HashMap<>();
		root.put("schema", schema);
		root.put("contentProvider", mProvider);
		generate(mProvider.getClassName(), outDirFile, root, templateContentProvider, mProvider.getJavaPackage());
	}


	protected File toJavaFilename(File outDirFile, String javaPackage, String javaClassName) {
		String packageSubPath = javaPackage.replace('.', '/');
		File packagePath = new File(outDirFile, packageSubPath);
		return new File(packagePath, String.format("%s.java", javaClassName));
	}

	protected File toFileForceExists(String filename) throws IOException {
		File file = new File(filename);
		if (!file.exists()) {
			throw new IOException(filename
					+ " does not exist. This check is to prevent accidental file generation into a wrong path.");
		}
		return file;
	}
}
