package com.excelente.geek_soccer.purchase_pack;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.inapp_util.IabHelper;
import com.excelente.geek_soccer.inapp_util.IabResult;
import com.excelente.geek_soccer.inapp_util.Inventory;
import com.excelente.geek_soccer.inapp_util.Purchase;
import com.excelente.geek_soccer.inapp_util.SkuDetails;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class PuchaseProcessDialog {
	public static String tag;
	public static Activity mActivity;
	public static IabHelper mHelper;
	public final static String base64PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjeNruRCrus/VpyFX13xIzZlKwe/SJiyGkT9MRyHO2qitpK/Z3iVAD+M47QATKdrfolaSpKlBR5KxORnUxphhf3nWGs78x0kNIOh6O5V4p5kii5xbDdL/3M1cFANZ+04Sm5soXZeEqTcFkby8IF8xMHsAz35IC4rxBP6Xb5MQnxSXdbAKO1r7T3HSE4ZMUoUuGdrtOS7/9zlKBVRrWTv0EefKp6YChCX6jE3934YrPQULic9JNGkf5i78+Dpw9LTyIhpjIFb81qU2xJ9L5Ev9+icKHTrMyA9p+NlKcokoE6tTfU6mdwFXCQvm7e1WHAyiC/eIv1LHMTvkUOAf5evc6QIDAQAB";
	public static boolean isSetup;
	public static Purchase purchaseOwned;
	boolean blnBind;
	public static String productIDSelect;
	
	public static void BuyCoinsDialog(Context contxt){
		
		final Dialog dialog = new Dialog(contxt);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		LayoutInflater factory = LayoutInflater.from(contxt);
		View DialogV = factory.inflate(R.layout.buy_coins_dialog, null);
		dialog.setContentView(DialogV);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		ImageView closeBt = (ImageView) DialogV.findViewById(R.id.close_icon);
		closeBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		}); 
		
		RadioGroup rb_Donate = (RadioGroup) DialogV.findViewById(R.id.buyGroup);
		
		productIDSelect = "coins_1";
		Query(productIDSelect);
		rb_Donate.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==R.id.buyCoins1){
					productIDSelect = "coins_1";
				}else if(checkedId==R.id.buyCoins2){
					productIDSelect = "coins_2";
				}
				Query(productIDSelect);
			}
		});
		
		Button CancelBtn = (Button)DialogV.findViewById(R.id.Cancel_btn);
		Button BuyBtn = (Button)DialogV.findViewById(R.id.Buy_btn);
		
		CancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		BuyBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Consume();
				purChase(productIDSelect);
			}
		});

		dialog.show();
	}
	
	public static void StartSetup(Activity activity) {
		tag = "geek_soccer";
		mActivity = activity;
		mHelper = new IabHelper(mActivity, base64PublicKey);

		mHelper.enableDebugLogging(true, tag);
		try {
			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
				@Override
				public void onIabSetupFinished(IabResult result) {
					boolean blnSuccess = result.isSuccess();
					boolean blnFail = result.isFailure();
 
					isSetup = blnSuccess;
 
					Log.i(tag, "mHelper.startSetup() ...");
					Log.i(tag, "	- blnSuccess return " + String.valueOf(blnSuccess));
					Log.i(tag, "	- blnFail return " + String.valueOf(blnFail));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
 
			isSetup = false;
			Log.w(tag, "mHelper.startSetup() - fail!");
		}
	}
	
	public static void FullStatPurchase(final String productID){
		if (!isSetup) return;
		 
		mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
			@Override
			public void onQueryInventoryFinished(IabResult result, Inventory inv) {
				boolean blnSuccess = result.isSuccess();
				boolean blnFail = result.isFailure();

				Log.i(tag, "mHelper.queryInventoryAsync() ...");
				Log.i(tag, "	- blnSuccess return " + String.valueOf(blnSuccess));
				Log.i(tag, "	- blnFail return " + String.valueOf(blnFail));

				if (!blnSuccess) return;
				
				Log.i(tag, "	- inv.hasPurchase()   = " + inv.hasPurchase(productID));
				Log.i(tag, "	- inv.getPurchase()   = " + inv.getPurchase(productID));
				Log.i(tag, "	- inv.hasDetails()    = " + inv.hasDetails(productID));
				Log.i(tag, "	- inv.getSkuDetails() = " + inv.getSkuDetails(productID));
				
				purchaseOwned=null;
				
				if (!inv.hasPurchase(productID)){// return;
					purChase(productID);
				}else{
					purchaseOwned = inv.getPurchase(productID);
					ConsumePuchase(productID);
					Log.i(tag, "	- inv.getPurchase() ...");
					Log.i(tag, "		.getDeveloperPayload() = " + purchaseOwned.getDeveloperPayload());
					Log.i(tag, "		.getItemType()         = " + purchaseOwned.getItemType());
					Log.i(tag, "		.getOrderId()          = " + purchaseOwned.getOrderId());
					Log.i(tag, "		.getOriginalJson()     = " + purchaseOwned.getOriginalJson());
					Log.i(tag, "		.getPackageName()      = " + purchaseOwned.getPackageName());
					Log.i(tag, "		.getPurchaseState()    = " + String.valueOf(purchaseOwned.getPurchaseState()));
					Log.i(tag, "		.getPurchaseTime()     = " + String.valueOf(purchaseOwned.getPurchaseTime()));
					Log.i(tag, "		.getSignature()        = " + purchaseOwned.getSignature());
					Log.i(tag, "		.getSku()              = " + purchaseOwned.getSku());
					Log.i(tag, "		.getToken()            = " + purchaseOwned.getToken());

					if (!inv.hasDetails(productID)) return;

					SkuDetails skuDetails = inv.getSkuDetails(productID);
					Log.i(tag, "	- inv.getSkuDetails() ...");
					Log.i(tag, "		.getDescription() = " + skuDetails.getDescription());
					Log.i(tag, "		.getPrice()       = " + skuDetails.getPrice());
					Log.i(tag, "		.getSku()         = " + skuDetails.getSku());
					Log.i(tag, "		.getTitle()       = " + skuDetails.getTitle());
					Log.i(tag, "		.getType()        = " + skuDetails.getType());
				}

				
			}
		});
	}

	public static void Query(final String productID){
		if (!isSetup) return;
		 
		mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
			@Override
			public void onQueryInventoryFinished(IabResult result, Inventory inv) {
				boolean blnSuccess = result.isSuccess();
				boolean blnFail = result.isFailure();

				Log.i(tag, "mHelper.queryInventoryAsync() ...");
				Log.i(tag, "	- blnSuccess return " + String.valueOf(blnSuccess));
				Log.i(tag, "	- blnFail return " + String.valueOf(blnFail));

				if (!blnSuccess) return;

				Log.i(tag, "	- inv.hasPurchase()   = " + inv.hasPurchase(productID));
				Log.i(tag, "	- inv.getPurchase()   = " + inv.getPurchase(productID));
				Log.i(tag, "	- inv.hasDetails()    = " + inv.hasDetails(productID));
				Log.i(tag, "	- inv.getSkuDetails() = " + inv.getSkuDetails(productID));

				if (!inv.hasPurchase(productID)) return;

				purchaseOwned = inv.getPurchase(productID);
				
				Log.i(tag, "	- inv.getPurchase() ...");
				Log.i(tag, "		.getDeveloperPayload() = " + purchaseOwned.getDeveloperPayload());
				Log.i(tag, "		.getItemType()         = " + purchaseOwned.getItemType());
				Log.i(tag, "		.getOrderId()          = " + purchaseOwned.getOrderId());
				Log.i(tag, "		.getOriginalJson()     = " + purchaseOwned.getOriginalJson());
				Log.i(tag, "		.getPackageName()      = " + purchaseOwned.getPackageName());
				Log.i(tag, "		.getPurchaseState()    = " + String.valueOf(purchaseOwned.getPurchaseState()));
				Log.i(tag, "		.getPurchaseTime()     = " + String.valueOf(purchaseOwned.getPurchaseTime()));
				Log.i(tag, "		.getSignature()        = " + purchaseOwned.getSignature());
				Log.i(tag, "		.getSku()              = " + purchaseOwned.getSku());
				Log.i(tag, "		.getToken()            = " + purchaseOwned.getToken());

				if (!inv.hasDetails(productID)) return;

				SkuDetails skuDetails = inv.getSkuDetails(productID);
				Log.i(tag, "	- inv.getSkuDetails() ...");
				Log.i(tag, "		.getDescription() = " + skuDetails.getDescription());
				Log.i(tag, "		.getPrice()       = " + skuDetails.getPrice());
				Log.i(tag, "		.getSku()         = " + skuDetails.getSku());
				Log.i(tag, "		.getTitle()       = " + skuDetails.getTitle());
				Log.i(tag, "		.getType()        = " + skuDetails.getType());
			}
		});
	}
	
	public static void purChase(String productID){
		if (!isSetup) return;
		mHelper.launchPurchaseFlow(mActivity, productID, 1001, new IabHelper.OnIabPurchaseFinishedListener() {
			@Override
			public void onIabPurchaseFinished(IabResult result, Purchase info) {
				
				boolean blnSuccess = result.isSuccess();
				boolean blnFail = result.isFailure();

				Log.i(tag, "mHelper.launchPurchaseFlow() - blnSuccess return " + String.valueOf(blnSuccess));
				Log.i(tag, "mHelper.launchPurchaseFlow() - blnFail return " + String.valueOf(blnFail));

				if (!blnSuccess) return;

				purchaseOwned = info;
				
				Log.d("TEST", "purchaseOwned.getDeveloperPayload()::"+purchaseOwned.getDeveloperPayload());
				Log.d("TEST", "purchaseOwned.getItemType()::"+purchaseOwned.getItemType());
				Log.d("TEST", "purchaseOwned.getOrderId()::"+purchaseOwned.getOrderId());
				Log.d("TEST", "purchaseOwned.getOriginalJson()::"+purchaseOwned.getOriginalJson());
				Log.d("TEST", "purchaseOwned.getPackageName()::"+purchaseOwned.getPackageName());
				Log.d("TEST", "purchaseOwned.getPurchaseState()::"+purchaseOwned.getPurchaseState());
				Log.d("TEST", "purchaseOwned.getPurchaseTime()::"+purchaseOwned.getPurchaseTime());
				Log.d("TEST", "purchaseOwned.getSignature()::"+purchaseOwned.getSignature());
				Log.d("TEST", "purchaseOwned.getSku()::"+purchaseOwned.getSku());
				Log.d("TEST", "purchaseOwned.getToken()::"+purchaseOwned.getToken());
			}
		},  MD5.md5Digest(productID));
	}
	
	public static void Consume(){
		if (!isSetup) return;
		if (purchaseOwned == null) return;

		mHelper.consumeAsync(purchaseOwned, new IabHelper.OnConsumeFinishedListener() {
			@Override
			public void onConsumeFinished(Purchase purchase, IabResult result) {
				boolean blnSuccess = result.isSuccess();
				boolean blnFail = result.isFailure();

				Toast.makeText(mActivity, "mHelper.consumeAsync() - blnSuccess return " + String.valueOf(blnSuccess), Toast.LENGTH_SHORT).show();
				Toast.makeText(mActivity, "mHelper.consumeAsync() - blnFail return " + String.valueOf(blnFail), Toast.LENGTH_SHORT).show();
				Log.i(tag, "mHelper.consumeAsync() ...");
				Log.i(tag, "	- blnSuccess return " + String.valueOf(blnSuccess));
				Log.i(tag, "	- blnFail return " + String.valueOf(blnFail));

				if (!blnSuccess) return;

				purchaseOwned = null;

				Log.i(tag, "	- purchase ...");
				Log.i(tag, "		.getDeveloperPayload() = " + purchase.getDeveloperPayload());
				Log.i(tag, "		.getItemType()         = " + purchase.getItemType());
				Log.i(tag, "		.getOrderId()          = " + purchase.getOrderId());
				Log.i(tag, "		.getOriginalJson()     = " + purchase.getOriginalJson());
				Log.i(tag, "		.getPackageName()      = " + purchase.getPackageName());
				Log.i(tag, "		.getPurchaseState()    = " + String.valueOf(purchase.getPurchaseState()));
				Log.i(tag, "		.getPurchaseTime()     = " + String.valueOf(purchase.getPurchaseTime()));
				Log.i(tag, "		.getSignature()        = " + purchase.getSignature());
				Log.i(tag, "		.getSku()              = " + purchase.getSku());
				Log.i(tag, "		.getToken()            = " + purchase.getToken());
			}
		});
	}
	
	public static void ConsumePuchase(final String productID){
		if (!isSetup) return;
		if (purchaseOwned == null) return;

		mHelper.consumeAsync(purchaseOwned, new IabHelper.OnConsumeFinishedListener() {
			@Override
			public void onConsumeFinished(Purchase purchase, IabResult result) {
				boolean blnSuccess = result.isSuccess();
				boolean blnFail = result.isFailure();

				Log.i(tag, "mHelper.consumeAsync() ...");
				Log.i(tag, "	- blnSuccess return " + String.valueOf(blnSuccess));
				Log.i(tag, "	- blnFail return " + String.valueOf(blnFail));

				if (!blnSuccess) return;
				purChase(productID);
				purchaseOwned = null;

				Log.i(tag, "	- purchase ...");
				Log.i(tag, "		.getDeveloperPayload() = " + purchase.getDeveloperPayload());
				Log.i(tag, "		.getItemType()         = " + purchase.getItemType());
				Log.i(tag, "		.getOrderId()          = " + purchase.getOrderId());
				Log.i(tag, "		.getOriginalJson()     = " + purchase.getOriginalJson());
				Log.i(tag, "		.getPackageName()      = " + purchase.getPackageName());
				Log.i(tag, "		.getPurchaseState()    = " + String.valueOf(purchase.getPurchaseState()));
				Log.i(tag, "		.getPurchaseTime()     = " + String.valueOf(purchase.getPurchaseTime()));
				Log.i(tag, "		.getSignature()        = " + purchase.getSignature());
				Log.i(tag, "		.getSku()              = " + purchase.getSku());
				Log.i(tag, "		.getToken()            = " + purchase.getToken());
			}
		});
	}
}
