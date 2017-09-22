package manifold.api.json;

import java.util.StringTokenizer;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import manifold.api.host.ITypeLoader;
import manifold.api.type.ClassType;
import manifold.api.type.JavaTypeManifold;
import manifold.ext.api.Structural;
import manifold.util.ManClassUtil;

/**
 */
public class JsonTypeManifold extends JavaTypeManifold<JsonModel>
{
  public static final String FILE_EXTENSION = "json";

  public void init( ITypeLoader typeLoader )
  {
    init( typeLoader, JsonModel::new, "editor.plugin.typeloader.json.JsonTypeFactory" );
  }

  @Override
  public boolean handlesFileExtension( String fileExtension )
  {
    return fileExtension.equals( FILE_EXTENSION );
  }

  @Override
  protected boolean isInnerType( String topLevel, String relativeInner )
  {
    JsonModel model = getModel( topLevel );
    IJsonParentType type = model == null ? null : model.getType();
    if( type == null )
    {
      return false;
    }
    IJsonParentType csr = type;
    for( StringTokenizer tokenizer = new StringTokenizer( relativeInner, "." ); tokenizer.hasMoreTokens(); )
    {
      String childName = tokenizer.nextToken();
      IJsonParentType child = csr.findChild( childName );
      if( child != null )
      {
        csr = child;
        continue;
      }
      return false;
    }
    return true;
  }

  @Override
  protected String produce( String topLevelFqn, String existing, JsonModel model, DiagnosticListener<JavaFileObject> errorHandler )
  {
    StringBuilder sb = new StringBuilder();
    sb.append( "package " ).append( ManClassUtil.getPackage( topLevelFqn ) ).append( ";\n\n" )
      .append( "import " ).append( Structural.class.getName() ).append( ";\n\n" );
    model.report( errorHandler );
    model.getType().render( sb, 0, true );
    return sb.toString();
  }

  @Override
  public ClassType getClassType( String fqn )
  {
    return ClassType.JavaClass;
  }
}