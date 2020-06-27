package com.meeting.tegal.ui.food_order

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meeting.models.Food
import com.example.meeting.utilities.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.meeting.tegal.R
import com.meeting.tegal.utilities.gone
import com.meeting.tegal.utilities.toast
import com.meeting.tegal.utilities.visible
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_select_food.*
import kotlinx.android.synthetic.main.bottom_sheet_selected_foods.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

class SelectFoodActivity : AppCompatActivity(), FoodClickInterface{
    private lateinit var bottomSheet: BottomSheetBehavior<*>
    private val foodViewModel: SelectFoodViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_food)
        setupRecyclerView()
        setupSelectedFoodRecyclerView()
        observe()
        setupBottomSheet()
        fetchFoods()
        onFinishSelectFood()
    }

    private fun onFinishSelectFood(){
        btn_ok.setOnClickListener {
            val selectedFoods = foodViewModel.listenToSelectedFoods().value
            if(selectedFoods.isNullOrEmpty()){
                finish()
            }else{
                val i = Intent()
                i.putParcelableArrayListExtra("selected_foods", selectedFoods as ArrayList<out Parcelable>)
                setResult(Activity.RESULT_OK, i)
                finish()
            }
        }
    }

    private fun getPassedIdPartner() = intent.getStringExtra("ID_MITRA")

    private fun bottomSheetBehavior(){
        if(bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED || bottomSheet.state == BottomSheetBehavior.STATE_HIDDEN){
            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }else{
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setupBottomSheet(){
        bottomSheet = BottomSheetBehavior.from(bottomsheet_detail_order)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        selectedFoods_button.setOnClickListener {
            bottomSheetBehavior()
        }
    }

    private fun setupRecyclerView(){
        rv_foods.apply {
            adapter = FoodAdapter(mutableListOf(), this@SelectFoodActivity)
            layoutManager = LinearLayoutManager(this@SelectFoodActivity)
        }
    }

    private fun setupSelectedFoodRecyclerView(){
        rv_selectedFoods.apply {
            adapter = SelectedFoodAdapter(mutableListOf(), this@SelectFoodActivity)
            layoutManager = LinearLayoutManager(this@SelectFoodActivity)
        }
    }

    private fun observe(){
        observeState()
        observeFoods()
        observeSelectedFoods()
    }

    private fun fetchFoods() = foodViewModel.getFoods(Constants.getToken(this), getPassedIdPartner())
    private fun observeSelectedFoods() = foodViewModel.listenToSelectedFoods().observe(this, Observer { handleSelectedFoods(it) })
    private fun observeFoods() = foodViewModel.listenToFoods().observe(this, Observer { handleFoods(it) })
    private fun observeState() = foodViewModel.listenToState().observer(this, Observer { handleState(it) })

    private fun handleSelectedFoods(selectedFoods: List<Food>){
        val totalPrice = selectedFoods.sumBy {
            it.price!! * it.qty!!
        }
        selectedFoods_totalPrice_textView.text = Constants.setToIDR(totalPrice)

        rv_selectedFoods.adapter?.let {
            if(it is SelectedFoodAdapter){
                it.updateList(selectedFoods)
            }
        }
    }

    private fun handleState(state: SelectFoodState){
        when(state){
            is SelectFoodState.Loading -> isLoading(state.isLoading)
            is SelectFoodState.ShowToast -> toast(state.message)
        }
    }

    private fun handleFoods(foods: List<Food>){
        rv_foods.adapter?.let { adapter ->
            if(adapter is FoodAdapter){
                adapter.updateList(foods)
            }
        }
    }

    private fun isLoading(b: Boolean){
        if(b){
            loading.visible()
        }else{
            loading.gone()
        }
    }

    override fun click(food: Food) = foodViewModel.addSelectedProduct(food)
    override fun increment(food: Food)  = foodViewModel.incrementQuantity(food)
    override fun decrement(food: Food) = foodViewModel.decrementQuantity(food)

    override fun onBackPressed() {
        if(bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED || bottomSheet.state == BottomSheetBehavior.STATE_HIDDEN){
            super.onBackPressed()
        }else{
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }
}

@Parcelize
data class OrderMakananResult(var idMakanan : List<Int> = mutableListOf(), var totalMakanan : List<Int> = mutableListOf()) : Parcelable