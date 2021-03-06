package com.ibm.wala.dalvik.ipa.callgraph.androidModel;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wala.dalvik.ipa.callgraph.impl.AndroidEntryPoint;
import com.ibm.wala.dalvik.ipa.callgraph.propagation.cfa.Intent;
import com.ibm.wala.dalvik.util.AndroidComponent;
import com.ibm.wala.dalvik.util.AndroidEntryPointManager;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.strings.Atom;

public class AndroidModelSome extends AndroidModel {
	private final List<TypeName> targets;
	
	public AndroidModelSome(
		final AndroidEntryPointManager manager,
		final IClassHierarchy cha,
		final AnalysisOptions options,
		final AnalysisCache cache,
		final List<String> intents) {
		super(manager, cha, options, cache);
		this.targets = new ArrayList<TypeName>(intents.size());
		for(String intent : intents) {
			this.targets.add(TypeName.findOrCreate(intent));
		}
		
	}
	@Override
	protected boolean selectEntryPoint(AndroidEntryPoint ep) {
		if (ep.belongsTo(AndroidComponent.APPLICATION) || ep.belongsTo(AndroidComponent.PROVIDER)) {
			return true;
		}
		final TypeName name = ep.getMethod().getDeclaringClass().getReference().getName();
		return this.targets.contains(name);
	}

}
