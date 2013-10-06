package com.innovativesoftware.menufactory;

import java.lang.reflect.Method;

import com.innovativesoftware.beanviewbuilder.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class BeanViewBuilder {
	private static Method findMatchingSetter(Method getter,Method[] methodList) {
		String methodName = getter.getName();
		String setterName = "";
		if (methodName.startsWith("get"))
			setterName= "set" + methodName.substring(3);
		if (methodName.startsWith("is"))
			setterName= "set" + methodName.substring(2);		
		
		for(Method m : methodList) {
			if (m.getName().equals(setterName)) {
				return m;
			}
		}
		return null;
	}
	
	private static Object invokeGetter(Method m, Object bean) {
		Object o = null;
		try {		
			o = m.invoke(bean);
		} catch (Exception e) {
			e.printStackTrace();
		} 		
		return o;
	}
	
	private static void invokeSetter(Method m, Object bean, Object value) {
		Class<?> typeList[] = m.getParameterTypes();
		if ((typeList.length == 1)) {
			try {
				m.invoke(bean, value);
			} catch (Exception e) {
				e.printStackTrace();
			} 						
		}
	}
	
	
	public static void buildView(LinearLayout parentLayout, Context context, Object bean,String resPackage) {
		Method[] methodList = bean.getClass().getMethods();
		
		for(Method m : methodList) {
			if (m.getName().startsWith("set")) {
				
			}
			
			if (m.getName().startsWith("is")) {
				LinearLayout ll = new LinearLayout(context);
				parentLayout.addView(ll);
				
				//TextView tvLabel = new TextView(context);
				LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				TextView tvLabel = (TextView)li.inflate(R.layout.tvtemplate, null);
				tvLabel.setText(m.getName().substring(2));	
				int textId = context.getResources().getIdentifier(m.getName().substring(2),"string",resPackage);
				if (textId != 0) {
					tvLabel.setText(textId);
				}				
				tvLabel.setPadding(10, 10, 10, 10);
				ll.addView(tvLabel);				
				
				Class<?> c = m.getReturnType();
				if (c.equals(boolean.class)) {
					Boolean b = (Boolean)invokeGetter(m,bean);
					Switch sw = new Switch(context);
					sw.setChecked(b);
					final Object fBean = bean;
					final Method fMethod = m;
					sw.setOnCheckedChangeListener(new OnCheckedChangeListener(){

						@Override
						public void onCheckedChanged(CompoundButton arg0,
								boolean arg1) {
							Method setter = findMatchingSetter(fMethod,fBean.getClass().getMethods());
							invokeSetter(setter,fBean,arg1);			
						}
						
					});
					
					ll.addView(sw);
				}
			}
			
			if (m.getName().startsWith("get")) {
				if (m.getName().equals("getClass"))
					continue;
				
				
				LinearLayout ll = new LinearLayout(context);
				parentLayout.addView(ll);

				TextView tvLabel = new TextView(context);
				tvLabel.setText(m.getName().substring(3));
				int textId = context.getResources().getIdentifier(m.getName().substring(3),"string",resPackage);
				if (textId != 0) {
					tvLabel.setText(textId);
				}				
				tvLabel.setPadding(10, 10, 10, 10);
				ll.addView(tvLabel);
				
				Class<?> c = m.getReturnType();
				if (c.equals(String.class)) {
					final Object fBean = bean;
					final Method fMethod = m;					
					
					String s = (String)invokeGetter(m,bean);
					TextView tv = new TextView(context);
					tv.setText(s);		
					ll.addView(tv);
					
					
					
					EditText et = new EditText(context);
					et.setText(s);					
					et.setOnFocusChangeListener(new OnFocusChangeListener() {
						
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (!hasFocus) {
								Method setter = findMatchingSetter(fMethod,fBean.getClass().getMethods());
								invokeSetter(setter,fBean, ((EditText)v).getText().toString());
							}
						}
					});
					ll.addView(et);
				
					
				}
			
				if (c.equals(int.class)) {

					Integer i = (Integer)invokeGetter(m,bean);
					final TextView tv = new TextView(context);
					tv.setText(i.toString());		
					ll.addView(tv);
					final Object fBean = bean;
					final Method fMethod = m;
					
					Button buttonDec = new Button(context);
					buttonDec.setText("-");
					buttonDec.setOnClickListener(new OnClickListener(){					
					@Override
					public void onClick(View arg0) {
						Integer i = (Integer)invokeGetter(fMethod,fBean);
						i = Integer.valueOf(i.intValue() - 1);
						Method setter = findMatchingSetter(fMethod,fBean.getClass().getMethods());
							invokeSetter(setter,fBean,i);
							tv.setText(i.toString());
					
					}});
					ll.addView(buttonDec);						
					
					Button buttonInc = new Button(context);
					buttonInc.setText("+");
					buttonInc.setOnClickListener(new OnClickListener(){					
					@Override
					public void onClick(View arg0) {
						Integer i = (Integer)invokeGetter(fMethod,fBean);
						i = Integer.valueOf(i.intValue() + 1);
						Method setter = findMatchingSetter(fMethod,fBean.getClass().getMethods());
							invokeSetter(setter,fBean,i);
							tv.setText(i.toString());
					
					}});
					ll.addView(buttonInc);
				}
				
				if (c.equals(float[].class)) {
					float fv[] = (float[])invokeGetter(m,bean);
					if ((fv != null)&&(fv.length < 4)) {
						for (int i = 0;i < fv.length;i++){
							final TextView tv = new TextView(context);
							tv.setText(fv[i]+"");		
							ll.addView(tv);
							final Object fBean = bean;
							final Method fMethod = m;
							final int fi = i;
							Button buttonDec = new Button(context);
							buttonDec.setText("-");
							buttonDec.setOnClickListener(new OnClickListener(){					
							@Override
							public void onClick(View arg0) {
								float fv[] = (float[])invokeGetter(fMethod,fBean);
								fv[fi] = fv[fi] + 1.0f;
								tv.setText(fv[fi]+"");
							
							}});
							ll.addView(buttonDec);						
							
							Button buttonInc = new Button(context);
							buttonInc.setText("+");
							buttonInc.setOnClickListener(new OnClickListener(){					
							@Override
							public void onClick(View arg0) {
								float fv[] = (float[])invokeGetter(fMethod,fBean);
								fv[fi] = fv[fi] - 1.0f;
								tv.setText(fv[fi]+"");
							}});
							ll.addView(buttonInc);
							
						}
					}
				}
			}				
		}			
	}
}

