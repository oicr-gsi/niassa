package net.sourceforge.seqware.pipeline.module;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;

public enum PluginMethod {
	clean_up {
		@Override
		public ReturnValue step(Plugin plugin) {
			return plugin.clean_up();
		}
	},
	do_run {
		@Override
		public ReturnValue step(Plugin plugin) {
			return plugin.do_run();
		}
	},
	do_test {
		@Override
		public ReturnValue step(Plugin plugin) {
			return plugin.do_test();
		}
	},
	init {
		@Override
		public ReturnValue step(Plugin plugin) {
			return plugin.init();
		}
	},
	parse_parameters {
		@Override
		public ReturnValue step(Plugin plugin) {
			return plugin.parse_parameters();
		}
	};

	public abstract ReturnValue step(Plugin plugin);
}
