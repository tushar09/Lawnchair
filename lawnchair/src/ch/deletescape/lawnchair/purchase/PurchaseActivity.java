/*
 *     Copyright (C) 2021 Lawnchair Team.
 *
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.purchase;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import ch.deletescape.lawnchair.util.myUtils.Constants;
import com.android.launcher3.R;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

public class PurchaseActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private Button cancel;
    private Button purchase;
    private TextView status;

    private BillingProcessor bp;
    private TransactionDetails purchaseTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_purchase);

        cancel = findViewById(R.id.btCancel);
        purchase = findViewById(R.id.btPurchase);
        status = findViewById(R.id.tvStatus);

        if(Constants.getSPreferences(this).isPaid()){
            status.setText("Congratulation, you have purchased the premium version");
            cancel.setText("OK");
            //purchase.setVisibility(View.GONE);
        }else {
            status.setText("The revenue earned from advertising enables us to keep the app development running and also to maintain the server cost. Please support us by purchasing the Premium version of this app. Premium version will remove all kinds of ads");
            purchase.setVisibility(View.VISIBLE);
            cancel.setText("CANCEL");
        }
        Log.e("ispaid", Constants.getSPreferences(this).isPaid() + "");

        bp = new BillingProcessor(this, getString(R.string.license), this);
        bp.initialize();

        purchase.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bp.isOneTimePurchaseSupported()){
                    bp.consumePurchase(getString(R.string.remove_ad_product_id));
                    bp.purchase(PurchaseActivity.this, getString(R.string.remove_ad_product_id));
                }
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Constants.getSPreferences(PurchaseActivity.this).setIsPaid(true);
        Log.e("Billing", "onProductPurchased: " + details.purchaseTime);
        finish();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
        purchaseTransaction = bp.getPurchaseTransactionDetails(getString(R.string.remove_ad_product_id));
        Constants.getSPreferences(PurchaseActivity.this).setIsPaid(isPurchased());
        if(Constants.getSPreferences(this).isPaid()){
            status.setText("Congratulation, you have purchased the premium version");
            purchase.setVisibility(View.GONE);
            cancel.setText("OK");
        }else {
            status.setText("The revenue earned from advertising enables us to keep the app development running and also to maintain the server cost. Please support us by purchasing the Premium version of this app. Premium version will remove all kinds of ads");
            purchase.setVisibility(View.VISIBLE);
            cancel.setText("CANCEL");
        }
    }

    public boolean isPurchased(){
        if(purchaseTransaction == null){
            return false;
        }
        if(purchaseTransaction.purchaseInfo != null){
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }
}