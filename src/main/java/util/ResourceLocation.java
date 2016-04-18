package util;

import java.net.URL;

public class ResourceLocation
{
	public URL resource;
	
	public ResourceLocation(String path)
	{
		this.resource = this.getClass().getResource(path);
	}
	
	public URL getPathURL()
	{
		return this.resource;
	}
	
	public String getPath()
	{
		return this.resource.toString().replace("file:/", "");
	}
}
