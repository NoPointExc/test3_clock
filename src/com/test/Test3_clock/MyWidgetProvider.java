package com.test.Test3_clock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class MyWidgetProvider extends AppWidgetProvider {

	/**
	 * when update,per 1000 Millis
	 * */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

	}

	/**
	 * when AppWidget be deleted
	 * */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	/**
	 * when last appWidget be deleted
	 * */
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}

	/**
	 * when enable
	 * */
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	/**
	 * receive broadcast
	 * */
	@Override
	public void onReceive(Context context, Intent intent) {

		super.onReceive(context, intent);
	}

}