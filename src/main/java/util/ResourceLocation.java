package util;

import java.net.URL;

public class ResourceLocation
{
	public URL resource;
	
	public ResourceLocation(String path)
	{
		this.resource = this.getClass().getResource(path);
	}
	
	public URL getPathURL(String path)
	{
		return this.resource;
	}
	
	public String getPath(String path)
	{
		return this.resource.toString().replace("file:", "");
	}
}
