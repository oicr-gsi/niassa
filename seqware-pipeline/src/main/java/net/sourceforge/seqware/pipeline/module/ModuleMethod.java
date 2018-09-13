package net.sourceforge.seqware.pipeline.module;

import net.sourceforge.seqware.common.module.ReturnValue;

/**
 * Enumeration class for ModuleMethod
 * 
 * Created by IntelliJ IDEA. User: xiao Date: 7/25/11 Time: 10:16 PM To change
 * this template use File | Settings | File Templates.
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public enum ModuleMethod {
	init {
		@Override
		public ReturnValue step(Module module) {
			return module.init();
		}
	},
	do_verify_parameters {
		@Override
		public ReturnValue step(Module module) {
			return module.do_verify_parameters();
		}
	},
	do_verify_input {
		@Override
		public ReturnValue step(Module module) {
			return module.do_verify_input();
		}
	},
	do_test {
		@Override
		public ReturnValue step(Module module) {
			return module.do_test();
		}
	},
	do_run {
		@Override
		public ReturnValue step(Module module) {
			return module.do_run();
		}
	},
	do_verify_output {
		@Override
		public ReturnValue step(Module module) {
			return module.do_verify_output();
		}
	},
	clean_up {
		@Override
		public ReturnValue step(Module module) {
			return module.clean_up();
		}
	};

	public abstract ReturnValue step(Module module);
}
