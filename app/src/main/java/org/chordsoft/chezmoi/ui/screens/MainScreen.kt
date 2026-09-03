package org.chordsoft.chezmoi.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.DefaultMapProperties
import com.google.maps.android.compose.DefaultMapUiSettings
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import org.chordsoft.chezmoi.R
import org.chordsoft.chezmoi.data.model.parseGeoJsonPolygon
import org.chordsoft.chezmoi.data.model.toLatLngList
import org.chordsoft.chezmoi.ui.components.LocationPermissionHandler
import org.chordsoft.chezmoi.ui.components.MapControlsOverlay
import org.chordsoft.chezmoi.ui.components.MarkerInfoDialog
import org.chordsoft.chezmoi.ui.components.rememberCustomMarkerIcon
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import kotlin.time.Duration.Companion.milliseconds

@OptIn(MapsComposeExperimentalApi::class, FlowPreview::class)
@Composable
fun MainScreen(modifier: Modifier, mapViewModel: MapViewModel) {
    var locationPermission = false
    val json =
        """
        {
          "coordinates": [
            [
              [
                5.224417525094311,
                46.19330958607206
              ],
              [
                5.22452164665741,
                46.19274272096338
              ],
              [
                5.224055658580984,
                46.19270319383142
              ],
              [
                5.223926245911084,
                46.19267870056082
              ],
              [
                5.224010528952558,
                46.19229419403972
              ],
              [
                5.224013613981917,
                46.1922743151719
              ],
              [
                5.224004175963071,
                46.1922654914175
              ],
              [
                5.223948588434717,
                46.192206222627966
              ],
              [
                5.223895520240601,
                46.19214510284161
              ],
              [
                5.223750445985576,
                46.1921182132822
              ],
              [
                5.223720452672791,
                46.192114295835886
              ],
              [
                5.223518604885076,
                46.19209482251217
              ],
              [
                5.22351909784344,
                46.19207499436454
              ],
              [
                5.223535017149221,
                46.19182785249083
              ],
              [
                5.223549274945626,
                46.191635694420604
              ],
              [
                5.223555956544753,
                46.1915445787594
              ],
              [
                5.223779507535731,
                46.19155551981629
              ],
              [
                5.223768590296042,
                46.19090262347763
              ],
              [
                5.223583888192828,
                46.190890021649615
              ],
              [
                5.223529799698913,
                46.19073973839763
              ],
              [
                5.223490388368459,
                46.190567547829644
              ],
              [
                5.22342768501216,
                46.19026879425461
              ],
              [
                5.223400650642374,
                46.19014590782282
              ],
              [
                5.223387763287034,
                46.190084001864484
              ],
              [
                5.223376573571056,
                46.19003197195158
              ],
              [
                5.223362207683617,
                46.18996559070575
              ],
              [
                5.223339718861024,
                46.189858830491005
              ],
              [
                5.223319073727475,
                46.18976554682812
              ],
              [
                5.223237096434351,
                46.189375284363834
              ],
              [
                5.223186479701082,
                46.1891186338084
              ],
              [
                5.223166419728988,
                46.18897579241032
              ],
              [
                5.223169469004846,
                46.18889105369328
              ],
              [
                5.223210560027903,
                46.188784851552306
              ],
              [
                5.223255702998739,
                46.18871460378741
              ],
              [
                5.223281970802567,
                46.18869066811246
              ],
              [
                5.223303236936562,
                46.18867133447241
              ],
              [
                5.223364297164091,
                46.18860978372266
              ],
              [
                5.223663864468816,
                46.18835258873315
              ],
              [
                5.223954831443745,
                46.188107272112084
              ],
              [
                5.22419300621099,
                46.18790352564449
              ],
              [
                5.224333303580349,
                46.187780968197835
              ],
              [
                5.224516203976337,
                46.18762154309798
              ],
              [
                5.224762168245912,
                46.18738611343074
              ],
              [
                5.225121975181309,
                46.18704756024273
              ],
              [
                5.225141980561065,
                46.187029151755006
              ],
              [
                5.225425650165379,
                46.186764155414814
              ],
              [
                5.22562904119586,
                46.186566491806516
              ],
              [
                5.226538525597565,
                46.185710895406714
              ],
              [
                5.226755689069568,
                46.18550124885435
              ],
              [
                5.226793142224862,
                46.185465382204356
              ],
              [
                5.22702537994257,
                46.18524372879798
              ],
              [
                5.227282840072388,
                46.18500446458777
              ],
              [
                5.22741883304746,
                46.18487207810717
              ],
              [
                5.227680721969374,
                46.185093056876845
              ],
              [
                5.227790731604664,
                46.18508729723387
              ],
              [
                5.22790242364832,
                46.18512294328366
              ],
              [
                5.228212475125641,
                46.18522046212546
              ],
              [
                5.228253198433356,
                46.185233176388635
              ],
              [
                5.228406764077842,
                46.185277910279346
              ],
              [
                5.228443489853082,
                46.18528800032437
              ],
              [
                5.228619285442769,
                46.18530527277466
              ],
              [
                5.228723327857568,
                46.185312241049246
              ],
              [
                5.228911863636534,
                46.185323858162775
              ],
              [
                5.228953480628252,
                46.185326645376946
              ],
              [
                5.229262943091952,
                46.1853457995843
              ],
              [
                5.229711439280821,
                46.185371233904874
              ],
              [
                5.230192664350621,
                46.18540413184769
              ],
              [
                5.230332174021012,
                46.185422113673184
              ],
              [
                5.230876448295165,
                46.18547449088617
              ],
              [
                5.231168354880502,
                46.18550839803071
              ],
              [
                5.231211377974858,
                46.1855138593525
              ],
              [
                5.231362551905451,
                46.18553161094157
              ],
              [
                5.231674045064199,
                46.18556873564096
              ],
              [
                5.231799080224949,
                46.18558159482802
              ],
              [
                5.231871947202301,
                46.18558737071784
              ],
              [
                5.231977739727419,
                46.185573582246896
              ],
              [
                5.23243200215996,
                46.185453856702864
              ],
              [
                5.232573164824977,
                46.185416851812896
              ],
              [
                5.232660936508904,
                46.18537458965713
              ],
              [
                5.232876850319509,
                46.185262246687884
              ],
              [
                5.233053285289667,
                46.18516779500785
              ],
              [
                5.233450264913468,
                46.184947395269944
              ],
              [
                5.233594812640672,
                46.184866181152294
              ],
              [
                5.233929570473728,
                46.184678532020605
              ],
              [
                5.234038343718518,
                46.18470612187011
              ],
              [
                5.233959253895495,
                46.18489775475342
              ],
              [
                5.23394347015063,
                46.1849241894956
              ],
              [
                5.233722324453733,
                46.18535373478627
              ],
              [
                5.233598484786495,
                46.18559309077246
              ],
              [
                5.233500099328642,
                46.18578870584568
              ],
              [
                5.233465156296496,
                46.18585425333686
              ],
              [
                5.233328561909533,
                46.18613079424831
              ],
              [
                5.233211015354911,
                46.18636552185022
              ],
              [
                5.23307134985447,
                46.18663041175477
              ],
              [
                5.232994675475653,
                46.186785962580466
              ],
              [
                5.232902105853204,
                46.18696524757321
              ],
              [
                5.23280619747771,
                46.18715811073179
              ],
              [
                5.232678425070331,
                46.18739664193726
              ],
              [
                5.232628697690732,
                46.18748139726989
              ],
              [
                5.232615357960473,
                46.18750418041021
              ],
              [
                5.23257426261237,
                46.18757795602467
              ],
              [
                5.232239918581935,
                46.188222320576735
              ],
              [
                5.232200507965811,
                46.188337501777085
              ],
              [
                5.232180404419652,
                46.18841717087355
              ],
              [
                5.23215186499296,
                46.18848079043755
              ],
              [
                5.232195330285039,
                46.18849705287942
              ],
              [
                5.232221983849705,
                46.18851454631627
              ],
              [
                5.232243709843336,
                46.18853844243005
              ],
              [
                5.232256290335234,
                46.18856071646323
              ],
              [
                5.232261131290989,
                46.188584043338956
              ],
              [
                5.232257083261619,
                46.18861204900906
              ],
              [
                5.232235183939129,
                46.188647612016624
              ],
              [
                5.232212734872054,
                46.188669673153285
              ],
              [
                5.232183512433349,
                46.188684660551694
              ],
              [
                5.232141256332648,
                46.18869810221284
              ],
              [
                5.232096041681569,
                46.188702593516204
              ],
              [
                5.232067418966886,
                46.188700453068364
              ],
              [
                5.232044649729768,
                46.18877837279165
              ],
              [
                5.232030659542232,
                46.18884891336029
              ],
              [
                5.23203840884734,
                46.18891182027168
              ],
              [
                5.232066894839356,
                46.18897431996822
              ],
              [
                5.232104453199198,
                46.18903664149771
              ],
              [
                5.232167442734055,
                46.18911828216405
              ],
              [
                5.232286538978626,
                46.189271789290096
              ],
              [
                5.232554589726053,
                46.18964307752609
              ],
              [
                5.232698630175589,
                46.18983573146077
              ],
              [
                5.232796528889636,
                46.18994641395377
              ],
              [
                5.232868226370126,
                46.19001887476913
              ],
              [
                5.232908037818929,
                46.19000908425684
              ],
              [
                5.232950917974408,
                46.19001094442196
              ],
              [
                5.233018643356295,
                46.19001772154314
              ],
              [
                5.23308958415882,
                46.19003974980786
              ],
              [
                5.233113279990243,
                46.19004829271834
              ],
              [
                5.233126790612165,
                46.19006153994916
              ],
              [
                5.233134370861044,
                46.190088416355046
              ],
              [
                5.233353246897924,
                46.19011204217877
              ],
              [
                5.233721696108829,
                46.19020930011315
              ],
              [
                5.234364107190594,
                46.190376842837686
              ],
              [
                5.234651175966805,
                46.19045047385244
              ],
              [
                5.235102089962488,
                46.19056592473022
              ],
              [
                5.23569548993404,
                46.190708299306195
              ],
              [
                5.236047917048491,
                46.190794154003584
              ],
              [
                5.236112122662656,
                46.1908100070013
              ],
              [
                5.236514646875221,
                46.19091559418003
              ],
              [
                5.237165963735103,
                46.191078441937755
              ],
              [
                5.237230206737034,
                46.191095194426424
              ],
              [
                5.237643214146047,
                46.19120327389605
              ],
              [
                5.238071776314053,
                46.191311045632276
              ],
              [
                5.238230367626138,
                46.19135116354289
              ],
              [
                5.238704993881568,
                46.191475141961895
              ],
              [
                5.238752204756916,
                46.191487724824626
              ],
              [
                5.239005366079907,
                46.19155660780862
              ],
              [
                5.239180918383593,
                46.19159909286637
              ],
              [
                5.239294929167811,
                46.191627475665484
              ],
              [
                5.239415530882778,
                46.19165843104233
              ],
              [
                5.239568792689671,
                46.19169504873625
              ],
              [
                5.240452040036716,
                46.191914565747446
              ],
              [
                5.240576862445404,
                46.191953544345964
              ],
              [
                5.240736883789834,
                46.19206029329809
              ],
              [
                5.240811039585923,
                46.19212909750714
              ],
              [
                5.240880019493718,
                46.19216647407287
              ],
              [
                5.2406086871037,
                46.19244658099043
              ],
              [
                5.240076759715406,
                46.19298405994469
              ],
              [
                5.239798717826901,
                46.1932588918935
              ],
              [
                5.239767601458022,
                46.19329103462599
              ],
              [
                5.239708852523172,
                46.19334534211223
              ],
              [
                5.239451517762761,
                46.1935873346743
              ],
              [
                5.239315017477336,
                46.19370713375445
              ],
              [
                5.239115309419602,
                46.19367683559279
              ],
              [
                5.238741660290781,
                46.1936436569873
              ],
              [
                5.238409635375158,
                46.19361326090463
              ],
              [
                5.238137711359161,
                46.193593391527976
              ],
              [
                5.23778272385012,
                46.19357245419754
              ],
              [
                5.237431661791329,
                46.19355233934487
              ],
              [
                5.237009532270275,
                46.19353902706889
              ],
              [
                5.236665245237067,
                46.19352598329346
              ],
              [
                5.236240780830371,
                46.19351902002889
              ],
              [
                5.235538280277765,
                46.193501308284354
              ],
              [
                5.2348276730515,
                46.193475643985096
              ],
              [
                5.234102992287129,
                46.19345475607931
              ],
              [
                5.233623723850158,
                46.193439851712995
              ],
              [
                5.233152159220043,
                46.19342299229903
              ],
              [
                5.23280002363442,
                46.193408289352085
              ],
              [
                5.232519396205896,
                46.19339758599322
              ],
              [
                5.232284060692796,
                46.19338419083142
              ],
              [
                5.231928783604371,
                46.19335603423167
              ],
              [
                5.231406061539631,
                46.19329332818299
              ],
              [
                5.231129667088768,
                46.19325911638617
              ],
              [
                5.230877753180965,
                46.19322082011064
              ],
              [
                5.230769330907472,
                46.193202228500816
              ],
              [
                5.229028674227001,
                46.19295080756202
              ],
              [
                5.228098308973808,
                46.192815904335234
              ],
              [
                5.227667658468934,
                46.19275227737769
              ],
              [
                5.227300885119924,
                46.19269640562794
              ],
              [
                5.226280327901673,
                46.19254433721564
              ],
              [
                5.225705044059292,
                46.19246461782113
              ],
              [
                5.225456952890917,
                46.19242443314526
              ],
              [
                5.225208862086148,
                46.19238424792794
              ],
              [
                5.225180460365035,
                46.192515425950674
              ],
              [
                5.225150908514144,
                46.192650229842904
              ],
              [
                5.225116427715606,
                46.19279143609829
              ],
              [
                5.22510220838912,
                46.1928565750339
              ],
              [
                5.225040803767151,
                46.193133434514884
              ],
              [
                5.22499883776012,
                46.193281993965954
              ],
              [
                5.224975892922409,
                46.19338784153606
              ],
              [
                5.224969157923791,
                46.193445627219354
              ],
              [
                5.224936571496499,
                46.193441760834354
              ],
              [
                5.224898161876079,
                46.19345422356291
              ],
              [
                5.224871527191406,
                46.19346915833785
              ],
              [
                5.224825467828381,
                46.193452943839205
              ],
              [
                5.224675588026122,
                46.19340362840584
              ],
              [
                5.224510501220918,
                46.19333118849748
              ],
              [
                5.224507872399964,
                46.19333033910057
              ],
              [
                5.224417525094311,
                46.19330958607206
              ]
            ]
          ],
          "type": "Polygon"
        }
    """.trimIndent()
    LocationPermissionHandler { locationPermission = true }
    val paris = LatLng(48.8540819, 2.3405084)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            paris,
            12f
        )
    }
    val markerInfoState = mapViewModel.markerInfo.collectAsStateWithLifecycle()
    var showMarkerInfoDialog by remember { mutableStateOf(false) }

    val franceBounds = LatLngBounds(
        LatLng(41.0, -5.5), // southwest
        LatLng(51.5, 9.8)  // northeast
    )

    val mapUiSettings = DefaultMapUiSettings.copy(
        compassEnabled = true,
        zoomControlsEnabled = false,
        myLocationButtonEnabled = locationPermission,
    )


    val polygon = parseGeoJsonPolygon(json)
    val points = toLatLngList(polygon)

    val pinState by mapViewModel.pinFlow.collectAsState()

    LaunchedEffect(Unit) {

        snapshotFlow {
            cameraPositionState.isMoving
        }
            .filter { moving -> !moving }
            .debounce(300.milliseconds)
            .collect {

                cameraPositionState
                    .projection
                    ?.visibleRegion
                    ?.latLngBounds
                    ?.let {
                        val s = it.southwest.latitude
                        val w = it.southwest.longitude
                        val n = it.northeast.latitude
                        val e = it.northeast.longitude
                        val z = cameraPositionState.position.zoom
                        Log.d("MAP_BOUNDS", "south=${s}&west=${w}&north=${n}&east=${e}&zoom=${z}")
                        mapViewModel.onCameraChange(s, w, n, e, z)
                    }
            }
    }

    Box(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = mapUiSettings,
            properties = DefaultMapProperties.copy(
                isMyLocationEnabled = locationPermission,
                minZoomPreference = 5f,
                maxZoomPreference = 18f,
                latLngBoundsForCameraTarget = franceBounds
            )
        ) {
            MapEffect(Unit) { map ->
                try {
                    map.isMyLocationEnabled = true
                    map.uiSettings.isMyLocationButtonEnabled = true
                    map.setPadding(0, 100, 0, 0)
                } catch (_: SecurityException) {
                }
            }
            Polygon(
                points = points,
                fillColor = Color.Red
            )

            pinState.rpls.values.forEach { pin ->
                Log.d("ARASH", pin.position.toString())

                key(pin.id) {
                    val markerState = rememberMarkerState(
                        key = pin.id.toString(),
                        position = pin.position
                    )
                    LaunchedEffect(pin.position) {
                        markerState.position = pin.position
                    }
                    val markerIcon = rememberCustomMarkerIcon(
                        iconRes = R.drawable.shield_with_house_24px,
                        text = pin.label,
                        backgroundColor = Color.White,
                        iconColor = Color(0xFF1976D2),
                        textColor = Color.Black,
                        borderColor = Color(0xFF444444)
                    )
                    Marker(
                        state = markerState,
                        icon = markerIcon,
                        onClick = {
                            if (pin.count == 1) {
                                mapViewModel.updateMarkerInfo(
                                    pin.id,
                                    MapViewModel.MarkerType.RplsMarker
                                )
                                showMarkerInfoDialog = true
                            }
                            false
                        }
                    )
                }
            }
        }
        MapControlsOverlay(cameraPositionState, {})
        if (showMarkerInfoDialog) {
            MarkerInfoDialog({ showMarkerInfoDialog = false }) {
                when (markerInfoState.value.type) {
                    MapViewModel.MarkerType.Unknown -> {
                        Text("Loading...")
                    }
                    MapViewModel.MarkerType.RplsMarker -> {
                        markerInfoState.value.text.forEach {
                            Text(it)
                        }
                    }
                }
            }
        }
    }
}
