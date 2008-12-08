package org.pentaho.ui.gwt.xul.generators;

import java.io.PrintWriter;

import org.pentaho.ui.xul.EventMethod;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class EventHandlerGenerator extends Generator {

  private String typeName;

  private String packageName;

  private String className;
  
  private TypeOracle typeOracle;
  
  private TreeLogger logger;
  
  private String handlerClassName;

  @Override
  public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
    this.typeName = typeName;
    this.logger = logger;
    typeOracle = context.getTypeOracle();

    try {
      // get classType and save instance variables 
      JClassType classType = typeOracle.getType(typeName);
      packageName = classType.getPackage().getName();
      handlerClassName = classType.getQualifiedSourceName();
      className = classType.getSimpleSourceName() + "Wrapper";
      // Generate class source code 
      generateClass(logger, context);

    } catch (Exception e) {

      // record to logger that Map generation threw an exception 
      logger.log(TreeLogger.ERROR, "PropertyMap ERROR!!!", e);

    }

    // return the fully qualifed name of the class generated 
    return packageName + "." + className;
  }

  private void generateClass(TreeLogger logger, GeneratorContext context) { 

    // get print writer that receives the source code 
    PrintWriter printWriter = null; 
    printWriter = context.tryCreate(logger, packageName, className); 
    // print writer if null, source code has ALREADY been generated, return
        if (printWriter == null) return; 

        // init composer, set class properties, create source writer 
    ClassSourceFileComposerFactory composer = null; 
    composer = new ClassSourceFileComposerFactory(packageName, className); 
    composer.addImplementedInterface("org.pentaho.ui.xul.gwt.util.EventHandlerWrapper");
    composer.addImport("org.pentaho.ui.xul.impl.XulEventHandler");
    
    SourceWriter sourceWriter = null; 
    sourceWriter = composer.createSourceWriter(context, printWriter); 

    // generator constructor source code 
    generateConstructor(sourceWriter); 
    
    generateMethods(sourceWriter);
    
    // close generated class 
    sourceWriter.outdent(); 
    sourceWriter.println("}"); 

    // commit generated class 
    context.commit(logger, printWriter); 

  }

  private void generateMethods(SourceWriter sourceWriter) {

    sourceWriter.println("public void execute(String method) { ");
    sourceWriter.indent();

    try{
      JClassType classType = typeOracle.getType(typeName);
      
      
      for(JMethod m : classType.getMethods()){
        String methodName = m.getName();
        boolean isEventMethod = (m.getAnnotation(EventMethod.class) != null);
        if(!isEventMethod){
          continue;
        }
        sourceWriter.println("if(method.equals(\""+methodName+"\")){");
        sourceWriter.indent();
        if(methodName.equals("setData")){
          //setData takes one parameter
          sourceWriter.println("handler.setData((Object) null);");
        } else {
          sourceWriter.println("handler."+methodName+"();");
        }
        sourceWriter.println("return;");
        sourceWriter.outdent();
        sourceWriter.println("}");
      } 
    }catch (Exception e) {

      // record to logger that Map generation threw an exception 
      logger.log(TreeLogger.ERROR, "PropertyMap ERROR!!!", e);

    }
    sourceWriter.println("System.err.println(\"ERROR: method '\" + method + \"' not annotated with EventMethod.\");");
    sourceWriter.outdent(); 
    sourceWriter.println("}");
   
    sourceWriter.println(handlerClassName+" handler;");

    sourceWriter.println("public void setHandler(XulEventHandler handler) { "); 
    sourceWriter.indent();
    sourceWriter.println("this.handler = ("+handlerClassName+") handler;");
    sourceWriter.outdent(); 
    sourceWriter.println("}");

    sourceWriter.println("public XulEventHandler getHandler() { "); 
    sourceWriter.indent();
    sourceWriter.println("return this.handler;");
    sourceWriter.outdent(); 
    sourceWriter.println("}");
    
    sourceWriter.println("public String getName() { "); 
    sourceWriter.indent();
    sourceWriter.println("return this.handler.getName();");
    sourceWriter.outdent(); 
    sourceWriter.println("}");

    sourceWriter.println("public Object getData() { "); 
    sourceWriter.indent();
    sourceWriter.println("return null;");
    sourceWriter.outdent(); 
    sourceWriter.println("}");
    
    sourceWriter.println("public void setData(Object o) { "); 
    sourceWriter.println("}");
    
  }
  
  private void generateConstructor(SourceWriter sourceWriter) { 


    // start constructor source generation 
    sourceWriter.println("public " + className + "() { "); 
    sourceWriter.indent(); 
    sourceWriter.println("super();"); 

    sourceWriter.outdent(); 
    sourceWriter.println("}"); 

  }
  
}
