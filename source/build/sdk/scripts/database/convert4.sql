/*
	This is slow!  
*/
DROP TABLE IF EXISTS `accounting`;
DROP TABLE IF EXISTS `example_table`;
DROP TABLE IF EXISTS `statistics`;

ALTER TABLE  `cached_items` ENGINE=InnoDB ;
ALTER TABLE  `data_records` ENGINE=InnoDB ;
ALTER TABLE  `folder_preferences` ENGINE=InnoDB ;
ALTER TABLE  `folders` ENGINE=InnoDB ;
ALTER TABLE  `groups` ENGINE=InnoDB ;
ALTER TABLE  `item_metadata` ENGINE=InnoDB ;
ALTER TABLE  `job_stats` ENGINE=InnoDB ;
ALTER TABLE  `record_fields` ENGINE=InnoDB ;
ALTER TABLE  `running_tasks` ENGINE=InnoDB ;
ALTER TABLE  `running_tasks_parameters` ENGINE=InnoDB ;
ALTER TABLE  `source_documents` ENGINE=InnoDB ;
ALTER TABLE  `sso` ENGINE=InnoDB ;
ALTER TABLE  `task_input_parameters` ENGINE=InnoDB ;
ALTER TABLE  `task_input_source_documents` ENGINE=InnoDB ;
ALTER TABLE  `task_log_messages` ENGINE=InnoDB ;
ALTER TABLE  `task_output_parameters` ENGINE=InnoDB ;
ALTER TABLE  `task_output_source_documents` ENGINE=InnoDB ;
ALTER TABLE  `task_properties` ENGINE=InnoDB ;
ALTER TABLE  `tasks` ENGINE=InnoDB ;
ALTER TABLE  `tgusage` ENGINE=InnoDB ;
ALTER TABLE  `tool_parameters` ENGINE=InnoDB ;
ALTER TABLE  `user_group_lookup` ENGINE=InnoDB ;
ALTER TABLE  `user_preferences` ENGINE=InnoDB ;
ALTER TABLE  `userdata` ENGINE=InnoDB ;
ALTER TABLE  `users` ENGINE=InnoDB ;
ALTER TABLE  `applications` ENGINE=InnoDB ;
