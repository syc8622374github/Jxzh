//
//  DfFuns
//
//  Created by Administrator on 2017-09-18 10:57:18
//  Copyright (c) Administrator All rights reserved.


/**
   
*/

package com.bocop.zyt.dataformat;

import com.mdx.framework.adapter.MAdapter;
import com.mdx.framework.server.api.Son;
import com.mdx.framework.widget.util.DataFormat;
import android.content.Context;
import com.bocop.zyt.ada.AdaFuns;

public class DfFuns implements DataFormat{
    int size = 1;

	@Override
	public boolean hasNext() {
		return size >= Integer.MAX_VALUE;
	}

	@Override
	public MAdapter<?> getAdapter(Context context, Son son, int page) {
		return new AdaFuns(context,null);
	}

	@Override
	public String[][] getPageNext() {
		return null;
	}

	@Override
	public void reload() {

	}
}
