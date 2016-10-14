/**
 * 
 */
package org.ngbw.sdk.api.data;

import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.common.util.Resource;

/**
 * @author hannes
 *
 */
public interface DatasetRegistryBuilder {

	public DatasetRegistry buildRegistry(Resource cfg, CoreRegistry coreRegistry);
}
