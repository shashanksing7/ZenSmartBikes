package com.example.zensmartbikes.Map

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.example.zensmartbikes.Map.MapBoxHelper.TurfHelper.TurfMeasurement

import com.example.zensmartbikes.Map.ServerHelper.getDataService
import com.example.zensmartbikes.R
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapLoaded
import com.mapbox.maps.MapLoadedCallback
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.layers.properties.generated.IconPitchAlignment
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.math.log
import kotlin.properties.Delegates


/*
This class will be used to animate the bike vector on the map according to the,
it extends the MapLoadedCallback class and overrides implements the run method ,which is only executed once the
map is completed loaded .
 */
public class AnimationHelper():MapLoadedCallback,AnimatorListener {

    lateinit var mapview:MapView
    var lng by Delegates.notNull<Double>()
    var  lat by Delegates.notNull<Double>()
    lateinit var context: Context

    /*
    Variables for the animation
     */
    private val AnimationDuration=3000L
    private var pointAnnotationManager: PointAnnotationManager? = null
    private  var animator: ValueAnimator? =null;
    private lateinit var  annotation: PointAnnotation;
    private  var mapLoadedOrNot=false;
    private var firstAnimationCompleted=false;

    /*
    Variable to store user decision to centre camera on Bike.
     */
    private var centreCameraOnBike:Boolean=true;
    private  val defaultZoom:Double= 18.0

    /*
    Setting the Style and camera options of the map in init block.
     */
//    init {
//
//       mapview.mapboxMap.apply {
//            setCamera(
//                cameraOptions {
//                    center(
//                        Point.fromLngLat(
//                            lng,
//                            lat
//                        )
//                    )
//                    zoom(defaultZoom)
//                }
//            )
//            loadStyle(Style.MAPBOX_STREETS)
//        }
//
//        mapview.mapboxMap.subscribeMapLoaded(this@AnimationHelper)
//    }
    override fun run(mapLoaded: MapLoaded) {

        /*
        Since map is loaded make the variable mapLoaded true.
         */

        mapLoadedOrNot=true
        /*
        Initializing the pointsAnnotationManager and adding the options
         */
        pointAnnotationManager=mapview.annotations.createPointAnnotationManager().apply {
            this.iconPitchAlignment = IconPitchAlignment.MAP
            /*
            Getting the bitmap.
             */
            bitmapFromDrawableRes(context,R.drawable.bike)?.let {
                /*
                adding the options to the manager.
                 */
               val options=PointAnnotationOptions()
                    .withIconImage(it)
                    .withPoint(Point.fromLngLat(lng,lat))

                annotation=create(options)


            }
        }



    }

    /*
    This method will bed used to animate the bike according to the geo-corordinates.
     */
      fun animate(lng:Double,lat:Double){

        Log.d("mytag", "animate: called")
        /*
        Cleaning the animation
         */
        cleanAnimator()
        val nextPoint=Point.fromLngLat(lng,lat)
        /*
        Initialising the animator object using the ValueAnimator class
         */
        animator= ValueAnimator.ofObject(
            ZenEvaluator(),
            annotation.point,
            nextPoint)
            .setDuration(AnimationDuration)

        /*
        Setting the icon rotating property using the TurfMeasurement class.
         */
        annotation.iconRotate=TurfMeasurement.bearing(annotation.point,nextPoint)
        /*
        Adding the interpolator ,which decides the rate of change of animated values between start point
        and end point.
         */
        animator?.interpolator=LinearInterpolator()
        /*
        Adding UpdateListener to listen for updates in the animated values and updating
        the points of our animation accordingly.
         */

        animator?.addUpdateListener { valueAnimator->

            /*
            Check if the camera should be centred or not.
             */
            checkZoom()

            (valueAnimator.animatedValue as Point).let {
                /*
                updating the points on our map.
                 */
                annotation.point=it

            }
            if(centreCameraOnBike){


                // Update camera position during animation
                val cameraPosition = CameraOptions.Builder()
                    .center(annotation.point)
                    .build()
                mapview.mapboxMap.setCamera(cameraPosition)
            }
        }


        animator?.start()
        animator?.addListener(this) // Add AnimatorListener

        /*
        Updating annotationManager.
         */
        animator?.addUpdateListener {
            /*
            updating the annotationManager
             */
            pointAnnotationManager?.update(annotation)
        }


    }


    /*
    This method will be used to clean the animator and release resources.
     */
    private  fun cleanAnimator(){

        animator?.removeAllListeners()
        animator?.cancel()
    }


    /*
    This class will be used to give the intermediate value between the start and end value
     */
    private class ZenEvaluator : TypeEvaluator<Point> {
        override fun evaluate(
            fraction: Float,
            startValue: Point,
            endValue: Point
        ): Point {
            val lat =
                startValue.latitude() + (endValue.latitude() - startValue.latitude()) * fraction
            val lon =
                startValue.longitude() + (endValue.longitude() - startValue.longitude()) * fraction
            return Point.fromLngLat(lon, lat)
        }
    }

    /*
    These methods are used to read the vector form drawable and return a bitmap.
     */
    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    /*
    This method will check the current zoom vs default zoom and accordingly update the centreCameraOnBike.
     */
    private  fun checkZoom(){
        /*
        Checking the current zoom.
         */
        val currentZoom:Double=mapview.mapboxMap.cameraState.zoom;
        /*
        This condition will make the centreCameraOnBike if zoom has change.
         */
        if(currentZoom!=defaultZoom){
            centreCameraOnBike=false
        }
    }

    /*
    This method will update the  centreCameraOnBike to true ,meaning the camera will follow the vehicle.
    This method will be executed when the user presses the my location button.
     */
    fun updateCentreCameraOnBike(){
        /*
        Making the variable true ,which means on animation updates the camera will follow the bike
         */
            if (::annotation.isInitialized) {
                mapview.mapboxMap.setCamera(CameraOptions.Builder()
                    .center(annotation.point)
                    .zoom(18.0)
                    .build())
                centreCameraOnBike = true
           }
    }

    /*
    This method will be used by our Fragment to check if the map is loaded ,
    if yes we will start animating,by calling the animate() method.
     */
    fun checkIfMapLoaded():Boolean{
        return mapLoadedOrNot
    }
    override fun onAnimationStart(animation: Animator) {
        Log.d("mytag", "onAnimationStart: started")
    }

    override fun onAnimationEnd(animation: Animator) {
        firstAnimationCompleted=true
        Log.d("mytag", "onAnimationEnd: ")
    }

    override fun onAnimationCancel(animation: Animator) {
        firstAnimationCompleted=true
        Log.d("mytag", "onAnimationCancel: ")
    }

    override fun onAnimationRepeat(animation: Animator) {
        Log.d("mytag", "onAnimationRepeat: ")
    }

    /*
    Check if first animation has completed or not.
     */
    fun checkIfFirstAnimationCompleted():Boolean{

        return  firstAnimationCompleted
    }

    /*
    This method sets the variables required.
     */
    fun setVariables(mapview:MapView, lng:Double,lat :Double,context: Context){
        this.mapview=mapview
        this.lat=lat
        this.lng=lng
        this.context=context
        /*
        Calling the initialize function to initialize map.
         */
        initializeMap()
    }

    /*
    Initialize map camera and subscribe to map loaded events
     */
    private fun initializeMap() {
       /*
       Setting the Style and camera options of the map in init block.
        */
        mapview.mapboxMap.apply {
            setCamera(
                cameraOptions {
                    center(
                        Point.fromLngLat(
                            lng,
                            lat
                        )
                    )
                    zoom(defaultZoom)
                }
            )
            loadStyle(Style.MAPBOX_STREETS)
            subscribeMapLoaded(this@AnimationHelper)
        }
    }


}
