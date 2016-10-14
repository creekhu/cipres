/**
 * 
 */
package org.ngbw.sdk.api.tool;

import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.common.util.Resource;

/**
 * @author hannes
 *
 */
public interface ToolRegistryBuilder {

	public ToolRegistry buildRegistry(Resource cfg, String extraTools, CoreRegistry coreRegistry);

}
