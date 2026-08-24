package com.lumyrinth.app.ui

import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumyrinth.app.audio.AmbientAudioController
import com.lumyrinth.app.audio.GuidanceSoundController
import com.lumyrinth.app.data.UserPreferences
import com.lumyrinth.app.data.UserPreferencesRepository
import com.lumyrinth.app.data.session.*
import com.lumyrinth.app.domain.*
import com.lumyrinth.app.haptics.HapticGuide
import com.lumyrinth.app.notifications.ReminderScheduler
import com.lumyrinth.app.ui.theme.LumyrinthColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.math.*

private enum class Screen { HOME, EXPLORE, PROGRESS, SETTINGS, DETAIL, COUNTDOWN, SESSION, COMPLETE, CUSTOM }
private data class Result(val exercise: BreathingExercise, val minutes: Int, val actual: Long, val cycles: Int, val complete: Boolean)

@Composable fun LumyrinthApp() {
    val context=LocalContext.current
    val prefsRepo=remember{UserPreferencesRepository(context)}
    val prefs by prefsRepo.preferences.collectAsState(initial=null)
    val repo=remember{SessionRepository.from(context)}
    val sessions by repo.sessions.collectAsState(initial=emptyList())
    val rhythms by repo.customRhythms.collectAsState(initial=emptyList())
    val scope=rememberCoroutineScope()
    var screen by remember{mutableStateOf(Screen.HOME)}
    var exercise by remember{mutableStateOf(PresetExercises.all.first())}
    var minutes by remember{mutableIntStateOf(3)}
    var result by remember{mutableStateOf<Result?>(null)}
    fun open(e:BreathingExercise){exercise=e;minutes=e.defaultMinutes;screen=Screen.DETAIL}
    fun finish(actual:Long,cycles:Int,complete:Boolean){
        result=Result(exercise,minutes,actual,cycles,complete)
        scope.launch{repo.save(SessionEntity(exerciseId=exercise.id,exerciseName=exercise.name,startedAtEpochMillis=System.currentTimeMillis()-actual,completedAtEpochMillis=System.currentTimeMillis(),targetDurationMillis=minutes*60_000L,actualDurationMillis=actual,cyclesCompleted=cycles,completed=complete))}
        screen=Screen.COMPLETE
    }
    val p=prefs?:return Box(Modifier.fillMaxSize().background(LumyrinthColor.Background))
    if(!p.onboardingComplete){
        Onboarding(p,{scope.launch{prefsRepo.setHaptics(it)}},{scope.launch{prefsRepo.setGuidanceSounds(it)}},{scope.launch{prefsRepo.completeOnboarding()};exercise=PresetExercises.all.first();minutes=1;screen=Screen.COUNTDOWN},{scope.launch{prefsRepo.completeOnboarding()};screen=Screen.EXPLORE});return
    }
    Box(Modifier.fillMaxSize().background(LumyrinthColor.Background)){
        AnimatedContent(screen,transitionSpec={fadeIn(tween(220)) togetherWith fadeOut(tween(160))},label="screen"){active->when(active){
            Screen.HOME->Home(sessions,::open){screen=Screen.PROGRESS}
            Screen.EXPLORE->Explore(rhythms.map{BreathingExercise(it.id,it.name,ExerciseCategory.CUSTOM,"Your personal breathing rhythm.",BreathingPattern(it.inhaleSeconds,it.holdAfterInhaleSeconds,it.exhaleSeconds,it.holdAfterExhaleSeconds),3)},::open){screen=Screen.CUSTOM}
            Screen.PROGRESS->Progress(sessions)
            Screen.SETTINGS->SettingsScreen(p,{scope.launch{prefsRepo.setHaptics(it)}},{scope.launch{prefsRepo.setGuidanceSounds(it)}},{scope.launch{prefsRepo.setKeepScreenAwake(it)}},{scope.launch{prefsRepo.setAmbientSound(it)}})
            Screen.DETAIL->Detail(exercise,minutes,{minutes=it},{screen=Screen.COUNTDOWN},{screen=Screen.EXPLORE})
            Screen.COUNTDOWN->Countdown(exercise,{screen=Screen.SESSION},{screen=Screen.DETAIL})
            Screen.SESSION->Session(exercise,minutes,p,::finish)
            Screen.COMPLETE->result?.let{Complete(it,{screen=Screen.HOME},{screen=Screen.COUNTDOWN})}
            Screen.CUSTOM->Custom({e->scope.launch{repo.saveCustomRhythm(CustomRhythmEntity(e.id,e.name,e.pattern.inhaleSeconds,e.pattern.holdAfterInhaleSeconds,e.pattern.exhaleSeconds,e.pattern.holdAfterExhaleSeconds,System.currentTimeMillis()))};open(e)},{screen=Screen.EXPLORE})
        }}
        if(screen in listOf(Screen.HOME,Screen.EXPLORE,Screen.PROGRESS,Screen.SETTINGS))Bottom(screen){screen=it}
    }
}

@Composable private fun Page(content:@Composable BoxScope.()->Unit)=Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top+WindowInsetsSides.Horizontal)).padding(20.dp,18.dp,20.dp,92.dp),content=content)
@Composable private fun Full(content:@Composable BoxScope.()->Unit)=Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(20.dp),content=content)

@Composable private fun Onboarding(p:UserPreferences,haptics:(Boolean)->Unit,sounds:(Boolean)->Unit,begin:()->Unit,explore:()->Unit){
    var step by remember{mutableIntStateOf(0)};var goals by remember{mutableStateOf(setOf("Relax","Focus","Unwind","Build a habit"))}
    Full{Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally){
        if(step in 1..2)Row(Modifier.fillMaxWidth()){RoundIcon(Icons.Rounded.ArrowBack,"Back"){step--}}
        Spacer(Modifier.weight(if(step==0).5f else .2f))
        when(step){
            0->{Text("L U M Y R I N T H",color=LumyrinthColor.TextPrimary,fontSize=17.sp,fontWeight=FontWeight.Medium);Spacer(Modifier.height(24.dp));Core(.7f,BreathPhase.INHALE,230.dp);Spacer(Modifier.height(26.dp));Text("Find your rhythm.",style=MaterialTheme.typography.headlineMedium,color=LumyrinthColor.TextPrimary);Text("Breathe. Focus. Unwind.",color=LumyrinthColor.TextSecondary)}
            1->{Heading("What are you\nlooking for?","Choose what matters most to you.");Spacer(Modifier.height(18.dp));listOf(Triple("Relax",Icons.Rounded.Spa,LumyrinthColor.Magenta),Triple("Focus",Icons.Rounded.CenterFocusStrong,LumyrinthColor.Magenta),Triple("Unwind",Icons.Rounded.Bedtime,LumyrinthColor.VioletBright),Triple("Sleep",Icons.Rounded.DarkMode,LumyrinthColor.VioletBright),Triple("Take a quick break",Icons.Rounded.Coffee,LumyrinthColor.Magenta),Triple("Build a habit",Icons.Rounded.Eco,LumyrinthColor.Positive)).forEach{(t,i,c)->Select(t,i,c,t in goals){goals=if(t in goals)goals-t else goals+t};Spacer(Modifier.height(7.dp))}}
            2->{Heading("Set your preferences","You can change these anytime.");Spacer(Modifier.height(24.dp));GuideCard("Haptic guidance","Feel inhale and exhale\nwith gentle vibrations.",p.hapticsEnabled,haptics,true);Spacer(Modifier.height(14.dp));GuideCard("Sound guidance","Play subtle sounds during\nyour sessions.",p.guidanceSoundsEnabled,sounds,false)}
            else->{Heading("Ready for your\nfirst session?","Start with a 1-minute session\nand feel the difference.");Spacer(Modifier.height(22.dp));Core(.72f,BreathPhase.INHALE,235.dp)}
        }
        Spacer(Modifier.weight(1f));when(step){0->Primary("Get Started"){step=1};1->Primary("Next",goals.isNotEmpty()){step=2};2->Primary("Next"){step=3};else->{Primary("Begin 1-Minute Session",click=begin);TextButton(explore){Text("Explore First",color=LumyrinthColor.TextPrimary)}}};Spacer(Modifier.height(14.dp));if(step<3)Row(horizontalArrangement=Arrangement.spacedBy(7.dp)){repeat(4){Dot(it==step)}}
    }}
}

@Composable private fun Home(sessions:List<SessionEntity>,open:(BreathingExercise)->Unit,progress:()->Unit)=Page{
    val summary=progressSummary(sessions);val recent=sessions.firstOrNull()?.let{s->PresetExercises.all.firstOrNull{it.id==s.exerciseId}}
    LazyColumn(verticalArrangement=Arrangement.spacedBy(13.dp),contentPadding=PaddingValues(bottom=12.dp)){
        item{Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(greeting(),color=LumyrinthColor.TextSecondary,fontSize=12.sp);Text("Find your rhythm.",style=MaterialTheme.typography.headlineMedium,color=LumyrinthColor.TextPrimary)};RoundIcon(Icons.Rounded.SelfImprovement,"Wellness"){}}}
        item{Quick{open(PresetExercises.all.first())}}
        item{Section("How do you want to feel?");Spacer(Modifier.height(8.dp));Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)){Feel("Calm",Icons.Rounded.Spa,LumyrinthColor.VioletBright){open(PresetExercises.all[0])};Feel("Focused",Icons.Rounded.CenterFocusStrong,LumyrinthColor.Magenta){open(PresetExercises.all[1])};Feel("Rested",Icons.Rounded.Bedtime,LumyrinthColor.Orange){open(PresetExercises.all[4])};Feel("Refreshed",Icons.Rounded.LightMode,LumyrinthColor.Orange){open(PresetExercises.all[5])}}}
        item{Section("Continue your rhythm");Spacer(Modifier.height(8.dp));ContinueCard(recent?:PresetExercises.all.first(),if(recent==null)"Explore" else "Repeat"){open(recent?:PresetExercises.all.first())}}
        item{ProgressStrip(summary,progress)}
    }
}

@Composable private fun Explore(custom:List<BreathingExercise>,open:(BreathingExercise)->Unit,create:()->Unit)=Page{
    var filter by remember{mutableStateOf<ExerciseCategory?>(null)};val filters=listOf("All" to null,"Relax" to ExerciseCategory.RELAX,"Focus" to ExerciseCategory.FOCUS,"Sleep" to ExerciseCategory.SLEEP,"Reset" to ExerciseCategory.RESET);val visible=PresetExercises.all.filter{filter==null||it.category==filter}
    LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=12.dp)){
        item{Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text("Explore",style=MaterialTheme.typography.headlineMedium,color=LumyrinthColor.TextPrimary,modifier=Modifier.weight(1f));Icon(Icons.Rounded.Search,"Search",tint=LumyrinthColor.TextPrimary)};Spacer(Modifier.height(12.dp));Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(7.dp)){filters.forEach{(t,c)->Pill(t,filter==c){filter=c}}}}
        if(filter==null)ExerciseCategory.entries.filter{it!=ExerciseCategory.CUSTOM}.forEach{cat->val group=visible.filter{it.category==cat};if(group.isNotEmpty()){item{Section(cat.title)};items(group,key={it.id}){ExerciseRow(it){open(it)}}}} else items(visible,key={it.id}){ExerciseRow(it){open(it)}}
        item{Section("My Rhythms")};item{ActionRow("Create your rhythm","Build a comfortable personal pattern",Icons.Rounded.Add,create)};items(custom,key={it.id}){ExerciseRow(it){open(it)}}
    }
}

@Composable private fun Detail(e:BreathingExercise,mins:Int,set:(Int)->Unit,begin:()->Unit,back:()->Unit)=Page{Column(Modifier.fillMaxSize()){
    Row(Modifier.fillMaxWidth()){RoundIcon(Icons.Rounded.ArrowBack,"Back",back);Spacer(Modifier.weight(1f));RoundIcon(Icons.Rounded.FavoriteBorder,"Favourite"){} };Spacer(Modifier.height(10.dp))
    Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(e.name,style=MaterialTheme.typography.headlineLarge,color=LumyrinthColor.TextPrimary);Text(e.description,color=LumyrinthColor.TextSecondary,lineHeight=20.sp)};Core(.7f,BreathPhase.INHALE,105.dp)};Spacer(Modifier.height(14.dp));Section("Pattern");Spacer(Modifier.height(7.dp));CardBox{e.pattern.phases().forEachIndexed{i,(p,s)->Pattern(p,s);if(i<e.pattern.phases().lastIndex)HorizontalDivider(color=LumyrinthColor.Border)}};Spacer(Modifier.height(14.dp));Section("Duration");Spacer(Modifier.height(8.dp));Row(horizontalArrangement=Arrangement.spacedBy(7.dp)){listOf(1,3,5,10).forEach{Pill("$it min",it==mins){set(it)}}};Spacer(Modifier.height(12.dp));CardBox{SettingLine(Icons.Rounded.VolumeUp,"Sound","On");HorizontalDivider(color=LumyrinthColor.Border);SettingLine(Icons.Rounded.Vibration,"Haptics","On")};Spacer(Modifier.weight(1f));Primary("Begin Session",click=begin)
}}

@Composable private fun Countdown(e:BreathingExercise,done:()->Unit,cancel:()->Unit){var n by remember{mutableIntStateOf(3)};BackHandler(onBack=cancel);LaunchedEffect(Unit){repeat(3){delay(1000);n--};done()};Full{RoundIcon(Icons.Rounded.Close,"Cancel",cancel);Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Text(e.name,color=LumyrinthColor.TextSecondary);Text(if(n>0)n.toString() else "Inhale",color=LumyrinthColor.TextPrimary,fontSize=58.sp,fontWeight=FontWeight.Light);Core((4-n)/4f,BreathPhase.INHALE,250.dp);Text("Get comfortable. Begin gently.",color=LumyrinthColor.TextSecondary)}}}

@Composable private fun Custom(save:(BreathingExercise)->Unit,back:()->Unit)=Page{var d by remember{mutableStateOf(CustomRhythmDraft())};Column(Modifier.fillMaxSize()){
    RoundIcon(Icons.Rounded.ArrowBack,"Back",back);Heading("Create your rhythm","Build a breathing pattern that\nworks for you.");Spacer(Modifier.height(14.dp));Stepper("Inhale",d.inhaleSeconds,LumyrinthColor.Violet){d=d.copy(inhaleSeconds=it.coerceIn(2,8))};Spacer(Modifier.height(7.dp));Stepper("Hold",d.holdAfterInhaleSeconds,LumyrinthColor.Magenta){d=d.copy(holdAfterInhaleSeconds=it.coerceIn(0,4))};Spacer(Modifier.height(7.dp));Stepper("Exhale",d.exhaleSeconds,LumyrinthColor.Orange){d=d.copy(exhaleSeconds=it.coerceIn(2,8))};Spacer(Modifier.height(7.dp));Stepper("Hold",d.holdAfterExhaleSeconds,Color(0xFFE0B04B)){d=d.copy(holdAfterExhaleSeconds=it.coerceIn(0,4))};Spacer(Modifier.height(10.dp));OutlinedTextField(d.name,{d=d.copy(name=it.take(24))},label={Text("Rhythm name")},singleLine=true,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(16.dp));Spacer(Modifier.height(10.dp));CardBox{Text("Preview",color=LumyrinthColor.TextPrimary);Wave(Modifier.fillMaxWidth().height(56.dp))};Spacer(Modifier.weight(1f));Primary("Save Rhythm"){save(d.asExercise())}
}}

@Composable private fun Session(e:BreathingExercise,mins:Int,p:UserPreferences,finish:(Long,Int,Boolean)->Unit){
    val context=LocalContext.current;val view=LocalView.current;var mark by remember{mutableLongStateOf(SystemClock.elapsedRealtime())};var held by remember{mutableLongStateOf(0)};var paused by remember{mutableStateOf(false)};var now by remember{mutableLongStateOf(mark)};var sound by remember{mutableStateOf(p.guidanceSoundsEnabled)};var haptic by remember{mutableStateOf(p.hapticsEnabled)};var dialog by remember{mutableStateOf(false)};val guide=remember{HapticGuide(context)};val sounds=remember{GuidanceSoundController(context)};val ambient=remember{AmbientAudioController(context)}
    DisposableEffect(Unit){view.keepScreenOn=p.keepScreenAwake;onDispose{view.keepScreenOn=false;sounds.release();ambient.release()}};LaunchedEffect(paused,p.ambientSound){if(paused)ambient.pause()else ambient.play(p.ambientSound)};LaunchedEffect(paused){while(!paused){now=SystemClock.elapsedRealtime();delay(32)}};val elapsed=if(paused)held else held+now-mark;val s=sessionSnapshot(e.pattern,mins*60_000L,elapsed)
    LaunchedEffect(s.phase,s.cyclesCompleted,paused){if(!paused&&haptic)guide.cue(s.phase);if(!paused&&sound)sounds.cue(s.phase)};LaunchedEffect(s.isFinishingCycle,s.phase,s.phaseProgress){if(s.isFinishingCycle&&s.phase==BreathPhase.EXHALE&&s.phaseProgress>.98f)finish(elapsed,s.cyclesCompleted,true)}
    fun pause(){if(paused){mark=SystemClock.elapsedRealtime();paused=false}else{held=elapsed;paused=true}};BackHandler{dialog=true}
    Full{Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally){Row(Modifier.fillMaxWidth()){RoundIcon(Icons.Rounded.Close,"End"){dialog=true};Spacer(Modifier.weight(1f));RoundIcon(Icons.Rounded.MusicNote,"Sound"){sound=!sound};Spacer(Modifier.width(7.dp));RoundIcon(Icons.Rounded.Vibration,"Haptics"){haptic=!haptic}};Spacer(Modifier.weight(.55f));Text(if(paused)"PAUSED" else s.phase.label.uppercase(),color=LumyrinthColor.TextPrimary,letterSpacing=3.sp,fontSize=13.sp);Spacer(Modifier.height(16.dp));Box(contentAlignment=Alignment.Center){Core(s.phaseProgress,s.phase,300.dp);Text(if(paused)"Ⅱ" else s.phaseRemainingSeconds.toString(),color=LumyrinthColor.TextPrimary,fontSize=52.sp,fontWeight=FontWeight.Light)};Spacer(Modifier.weight(.45f));Text(if(s.isFinishingCycle)"Finishing cycle…" else "%d:%02d remaining".format(s.targetRemainingMillis/60000,(s.targetRemainingMillis/1000)%60),color=LumyrinthColor.TextSecondary);Spacer(Modifier.height(10.dp));LinearProgressIndicator({(elapsed.toFloat()/(mins*60_000L)).coerceIn(0f,1f)},Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),color=LumyrinthColor.VioletBright,trackColor=LumyrinthColor.SurfaceSoft);Spacer(Modifier.height(22.dp));Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceAround,verticalAlignment=Alignment.CenterVertically){RoundIcon(if(sound)Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,"Sound"){sound=!sound};BigIcon(if(paused)Icons.Rounded.PlayArrow else Icons.Rounded.Pause,"Pause",::pause);RoundIcon(Icons.Rounded.Vibration,"Haptic"){haptic=!haptic}}}}
    if(dialog)AlertDialog({dialog=false},containerColor=LumyrinthColor.SurfaceHigh,title={Text("End this session?")},text={Text("Your time will be saved as ended early.")},confirmButton={TextButton({finish(elapsed,s.cyclesCompleted,false)}){Text("End session",color=LumyrinthColor.Magenta)}},dismissButton={TextButton({dialog=false}){Text("Keep breathing",color=LumyrinthColor.VioletBright)}})
}

@Composable private fun Complete(r:Result,done:()->Unit,repeat:()->Unit)=Full{var feeling by remember{mutableStateOf<String?>(null)};Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally){Spacer(Modifier.weight(.35f));Text(if(r.complete)"Complete" else "Session ended",style=MaterialTheme.typography.headlineLarge,color=LumyrinthColor.TextPrimary);Text(if(r.complete)"You've found your rhythm." else "You still made time for yourself.",color=LumyrinthColor.TextSecondary);Spacer(Modifier.height(18.dp));Box(contentAlignment=Alignment.Center){Core(.78f,BreathPhase.HOLD_AFTER_INHALE,250.dp);Icon(if(r.complete)Icons.Rounded.Check else Icons.Rounded.Stop,null,tint=LumyrinthColor.TextPrimary,modifier=Modifier.size(56.dp))};Row(horizontalArrangement=Arrangement.spacedBy(60.dp)){Metric("${r.actual/60000}","Duration","min");Metric(r.cycles.toString(),"Cycles")};Spacer(Modifier.height(24.dp));if(r.complete)CardBox{Text("How do you feel?",color=LumyrinthColor.TextPrimary,modifier=Modifier.fillMaxWidth(),textAlign=TextAlign.Center);Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceAround){listOf("Better" to Icons.Rounded.SentimentSatisfiedAlt,"Same" to Icons.Rounded.SentimentNeutral,"Not great" to Icons.Rounded.SentimentDissatisfied).forEach{(t,i)->MoodChoice(t,i,feeling==t){feeling=t}}}};Spacer(Modifier.weight(1f));Primary("Done",click=done);TextButton(repeat){Text(if(r.complete)"Repeat Session" else "Restart",color=LumyrinthColor.TextSecondary)}}}

@Composable private fun Progress(sessions:List<SessionEntity>)=Page{val s=progressSummary(sessions);val week=weekMinutes(sessions);LazyColumn(verticalArrangement=Arrangement.spacedBy(14.dp),contentPadding=PaddingValues(bottom=12.dp)){
    item{Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Your Rhythm",style=MaterialTheme.typography.headlineMedium,color=LumyrinthColor.TextPrimary);Text("${s.currentRhythmDays} day rhythm",color=LumyrinthColor.VioletBright)};Box(Modifier.size(50.dp).background(LumyrinthColor.Orange.copy(.1f),CircleShape),contentAlignment=Alignment.Center){Icon(Icons.Rounded.LocalFireDepartment,null,tint=LumyrinthColor.Orange)}}}
    item{CardBox{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceAround){SmallMetric("Today","${s.todayMinutes} min");SmallMetric("This week","${week.sum()} min");SmallMetric("Sessions",sessions.count{it.completed}.toString())}}}
    item{Section("This week");Spacer(Modifier.height(8.dp));WeekChart(week)};item{Row{Section("Calendar",Modifier.weight(1f));Text(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")),color=LumyrinthColor.TextSecondary,fontSize=12.sp)};Spacer(Modifier.height(8.dp));Calendar(sessions)};item{Section("Recent sessions")};if(sessions.isEmpty())item{CardBox{Text("Your completed sessions will appear here.",color=LumyrinthColor.TextSecondary)}}else items(sessions.take(5),key={it.id}){ActionRow(it.exerciseName,"${if(it.completed)"Completed" else "Ended early"} · ${it.actualDurationMillis/60000} min",Icons.Rounded.ChevronRight){}}
}}

@Composable private fun SettingsScreen(p:UserPreferences,h:(Boolean)->Unit,s:(Boolean)->Unit,k:(Boolean)->Unit,a:(String)->Unit)=Page{val context=LocalContext.current;var reminder by remember{mutableStateOf(false)};LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=12.dp)){
    item{Text("Settings",style=MaterialTheme.typography.headlineMedium,color=LumyrinthColor.TextPrimary);Text("Quietly yours.",color=LumyrinthColor.TextSecondary)};item{Section("Guidance")};item{ToggleRow("Haptic guidance","Soft cues at each breath phase",Icons.Rounded.Vibration,p.hapticsEnabled,h)};item{ToggleRow("Sound guidance","Gentle tonal guidance",Icons.Rounded.VolumeUp,p.guidanceSoundsEnabled,s)};item{ToggleRow("Keep screen awake","Only while a session is open",Icons.Rounded.LightMode,p.keepScreenAwake,k)};item{Section("Ambient sound")};item{Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(7.dp)){listOf("None","Rain","Night","Ocean","Forest","Fireplace","Stream","Deep Space").forEach{Pill(it,p.ambientSound==it){a(it)}}}};item{Section("Your practice")};item{ToggleRow("Daily reminder","A quiet invitation once a day",Icons.Rounded.Notifications,reminder){reminder=it;if(it)ReminderScheduler.enableDaily(context)else ReminderScheduler.disable(context)}};item{ActionRow("Your data","Your practice stays on this device",Icons.Rounded.Lock){}};item{ActionRow("About Lumyrinth","General wellness, not a medical device",Icons.Rounded.Info){}}
}}

@Composable private fun BoxScope.Bottom(current:Screen,go:(Screen)->Unit){val tabs=listOf(Triple(Icons.Rounded.Home,"Home",Screen.HOME),Triple(Icons.Rounded.Explore,"Explore",Screen.EXPLORE),Triple(Icons.Rounded.BarChart,"Progress",Screen.PROGRESS),Triple(Icons.Rounded.Settings,"Settings",Screen.SETTINGS));NavigationBar(Modifier.align(Alignment.BottomCenter).fillMaxWidth(),containerColor=LumyrinthColor.BackgroundElevated,tonalElevation=0.dp){tabs.forEach{(i,t,d)->NavigationBarItem(current==d,{go(d)},{Icon(i,t,Modifier.size(21.dp))},label={Text(t,fontSize=10.sp)},colors=NavigationBarItemDefaults.colors(selectedIconColor=LumyrinthColor.VioletBright,selectedTextColor=LumyrinthColor.VioletBright,unselectedIconColor=LumyrinthColor.TextTertiary,unselectedTextColor=LumyrinthColor.TextTertiary,indicatorColor=Color.Transparent))}}}
@Composable private fun Heading(t:String,sub:String){Text(t,color=LumyrinthColor.TextPrimary,style=MaterialTheme.typography.headlineMedium,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth());Spacer(Modifier.height(5.dp));Text(sub,color=LumyrinthColor.TextSecondary,fontSize=12.sp,lineHeight=17.sp,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth())}
@Composable private fun Primary(t:String,enabled:Boolean=true,click:()->Unit)=Button(click,Modifier.fillMaxWidth().height(56.dp),enabled,colors=ButtonDefaults.buttonColors(containerColor=LumyrinthColor.Violet,disabledContainerColor=LumyrinthColor.SurfaceSoft),shape=RoundedCornerShape(28.dp)){Text(t,fontWeight=FontWeight.SemiBold)}
@Composable private fun RoundIcon(i:ImageVector,d:String,click:()->Unit)=IconButton(click,Modifier.size(42.dp).background(LumyrinthColor.Surface,CircleShape).border(1.dp,LumyrinthColor.Border,CircleShape)){Icon(i,d,tint=LumyrinthColor.TextPrimary,modifier=Modifier.size(20.dp))}
@Composable private fun BigIcon(i:ImageVector,d:String,click:()->Unit)=IconButton(click,Modifier.size(64.dp).background(LumyrinthColor.Surface,CircleShape).border(1.dp,LumyrinthColor.VioletBright,CircleShape)){Icon(i,d,tint=LumyrinthColor.TextPrimary,modifier=Modifier.size(28.dp))}
@Composable private fun Dot(on:Boolean)=Box(Modifier.size(if(on)6.dp else 5.dp).background(if(on)LumyrinthColor.VioletBright else LumyrinthColor.SurfaceSoft,CircleShape))
@Composable private fun Section(t:String,modifier:Modifier=Modifier)=Text(t,color=LumyrinthColor.TextPrimary,fontSize=15.sp,fontWeight=FontWeight.SemiBold,modifier=modifier)
@Composable private fun CardBox(content:@Composable ColumnScope.()->Unit)=Column(Modifier.fillMaxWidth().background(LumyrinthColor.Surface,RoundedCornerShape(19.dp)).border(1.dp,LumyrinthColor.Border,RoundedCornerShape(19.dp)).padding(14.dp),content=content)
@Composable private fun Pill(t:String,on:Boolean,click:()->Unit)=Text(t,color=if(on)LumyrinthColor.TextPrimary else LumyrinthColor.TextSecondary,modifier=Modifier.background(if(on)LumyrinthColor.Violet else LumyrinthColor.Surface,CircleShape).border(1.dp,if(on)LumyrinthColor.VioletBright.copy(.35f)else LumyrinthColor.Border,CircleShape).clickable(onClick=click).padding(13.dp,8.dp),fontSize=11.sp)

@Composable private fun Select(t:String,i:ImageVector,c:Color,on:Boolean,click:()->Unit)=Row(Modifier.fillMaxWidth().height(51.dp).background(LumyrinthColor.Surface,RoundedCornerShape(14.dp)).border(1.dp,if(on)LumyrinthColor.Violet.copy(.4f)else LumyrinthColor.Border,RoundedCornerShape(14.dp)).clickable(onClick=click).padding(horizontal=13.dp),verticalAlignment=Alignment.CenterVertically){Icon(i,null,tint=c,modifier=Modifier.size(21.dp));Spacer(Modifier.width(12.dp));Text(t,color=LumyrinthColor.TextPrimary,fontSize=13.sp,modifier=Modifier.weight(1f));Box(Modifier.size(22.dp).background(if(on)LumyrinthColor.Violet else Color.Transparent,RoundedCornerShape(7.dp)).border(1.dp,if(on)LumyrinthColor.Violet else LumyrinthColor.Border,RoundedCornerShape(7.dp)),contentAlignment=Alignment.Center){if(on)Icon(Icons.Rounded.Check,null,tint=Color.White,modifier=Modifier.size(15.dp))}}
@Composable private fun GuideCard(t:String,sub:String,on:Boolean,change:(Boolean)->Unit,haptic:Boolean)=Column(Modifier.fillMaxWidth().height(132.dp).background(Brush.linearGradient(listOf(Color(0xFF551274),Color(0xFF2A1048),Color(0xFF46172D))),RoundedCornerShape(22.dp)).border(1.dp,LumyrinthColor.Magenta.copy(.35f),RoundedCornerShape(22.dp)).padding(15.dp)){Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(t,color=LumyrinthColor.TextPrimary,fontWeight=FontWeight.Medium);Text(sub,color=LumyrinthColor.TextSecondary,fontSize=11.sp,lineHeight=16.sp)};Switch(on,change,colors=SwitchDefaults.colors(checkedTrackColor=LumyrinthColor.Violet,checkedThumbColor=Color.White))};Spacer(Modifier.weight(1f));if(haptic)Dotted(Modifier.fillMaxWidth().height(24.dp))else Wave(Modifier.fillMaxWidth().height(26.dp))}

@Composable private fun Quick(click:()->Unit)=Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF571476),Color(0xFF44113F),Color(0xFF3A102A))),RoundedCornerShape(28.dp)).border(1.dp,LumyrinthColor.Magenta.copy(.42f),RoundedCornerShape(28.dp)).padding(17.dp)){Row{Text("Quick Breathe",color=LumyrinthColor.TextSecondary,fontSize=11.sp,modifier=Modifier.weight(1f));Text("3 min",color=LumyrinthColor.TextPrimary,fontSize=10.sp,modifier=Modifier.background(Color.White.copy(.08f),CircleShape).padding(8.dp,4.dp))};Text("Relax",color=LumyrinthColor.TextPrimary,fontSize=24.sp);Text("4 sec inhale  ·  6 sec exhale",color=LumyrinthColor.TextSecondary,fontSize=11.sp);Spacer(Modifier.height(12.dp));Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.weight(1f).height(47.dp).background(Color.White.copy(.12f),CircleShape).clickable(onClick=click),contentAlignment=Alignment.Center){Text("Start",color=LumyrinthColor.TextPrimary,fontWeight=FontWeight.SemiBold)};Spacer(Modifier.width(9.dp));RoundIcon(Icons.Rounded.PlayArrow,"Start",click)}}
@Composable private fun Feel(t:String,i:ImageVector,c:Color,click:()->Unit)=Row(Modifier.widthIn(min=102.dp).height(49.dp).background(LumyrinthColor.Surface,RoundedCornerShape(14.dp)).border(1.dp,c.copy(.25f),RoundedCornerShape(14.dp)).clickable(onClick=click).padding(horizontal=11.dp),verticalAlignment=Alignment.CenterVertically){Icon(i,null,tint=c,modifier=Modifier.size(19.dp));Spacer(Modifier.width(7.dp));Text(t,color=LumyrinthColor.TextPrimary,fontSize=11.sp)}
@Composable private fun ContinueCard(e:BreathingExercise,label:String,click:()->Unit)=Row(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(LumyrinthColor.Surface,Color(0xFF2D111F))),RoundedCornerShape(18.dp)).border(1.dp,LumyrinthColor.Magenta.copy(.2f),RoundedCornerShape(18.dp)).clickable(onClick=click).padding(13.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Rounded.Spa,null,tint=LumyrinthColor.Magenta,modifier=Modifier.size(27.dp));Spacer(Modifier.width(11.dp));Column(Modifier.weight(1f)){Text(e.name,color=LumyrinthColor.TextPrimary,fontWeight=FontWeight.Medium);Text("${e.defaultMinutes} minutes",color=LumyrinthColor.TextSecondary,fontSize=10.sp)};Text(label,color=LumyrinthColor.TextPrimary,fontSize=10.sp,modifier=Modifier.background(LumyrinthColor.Violet.copy(.35f),CircleShape).padding(11.dp,7.dp));Icon(Icons.Rounded.ChevronRight,null,tint=LumyrinthColor.TextSecondary,modifier=Modifier.size(18.dp))}
@Composable private fun ProgressStrip(s:ProgressSummary,click:()->Unit)=Row(Modifier.fillMaxWidth().background(LumyrinthColor.Surface,RoundedCornerShape(17.dp)).border(1.dp,LumyrinthColor.Border,RoundedCornerShape(17.dp)).clickable(onClick=click).padding(12.dp),verticalAlignment=Alignment.CenterVertically){Text("Today's progress",color=LumyrinthColor.TextSecondary,fontSize=9.sp,modifier=Modifier.weight(1.2f));SmallMetric("Sessions",s.todaySessions.toString(),Modifier.weight(.7f));SmallMetric("Mindful","${s.todayMinutes} min",Modifier.weight(.8f));SmallMetric("Rhythm","${s.currentRhythmDays} day",Modifier.weight(.8f));Icon(Icons.Rounded.LocalFireDepartment,null,tint=LumyrinthColor.Orange,modifier=Modifier.size(23.dp))}

@Composable private fun ExerciseRow(e:BreathingExercise,click:()->Unit)=Row(Modifier.fillMaxWidth().background(LumyrinthColor.Surface,RoundedCornerShape(16.dp)).border(1.dp,LumyrinthColor.Border,RoundedCornerShape(16.dp)).clickable(onClick=click).padding(10.dp),verticalAlignment=Alignment.CenterVertically){MiniCore(45.dp);Spacer(Modifier.width(10.dp));Column(Modifier.weight(1f)){Text(e.name,color=LumyrinthColor.TextPrimary,fontWeight=FontWeight.Medium,fontSize=13.sp);Text(patternText(e.pattern),color=LumyrinthColor.TextSecondary,fontSize=9.sp,maxLines=1,overflow=TextOverflow.Ellipsis);Text("${e.defaultMinutes}–10 min",color=LumyrinthColor.TextTertiary,fontSize=9.sp)};Icon(Icons.Rounded.ChevronRight,null,tint=LumyrinthColor.TextSecondary,modifier=Modifier.size(18.dp))}
@Composable private fun ActionRow(t:String,sub:String,i:ImageVector,click:()->Unit)=Row(Modifier.fillMaxWidth().background(LumyrinthColor.Surface,RoundedCornerShape(17.dp)).border(1.dp,LumyrinthColor.Border,RoundedCornerShape(17.dp)).clickable(onClick=click).padding(14.dp),verticalAlignment=Alignment.CenterVertically){Icon(i,null,tint=LumyrinthColor.VioletBright,modifier=Modifier.size(21.dp));Spacer(Modifier.width(11.dp));Column(Modifier.weight(1f)){Text(t,color=LumyrinthColor.TextPrimary,fontWeight=FontWeight.Medium);Text(sub,color=LumyrinthColor.TextSecondary,fontSize=10.sp)};Icon(Icons.Rounded.ChevronRight,null,tint=LumyrinthColor.TextTertiary,modifier=Modifier.size(18.dp))}
@Composable private fun Pattern(p:BreathPhase,s:Int)=Row(Modifier.fillMaxWidth().height(40.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(8.dp).background(phaseColor(p),CircleShape));Spacer(Modifier.width(11.dp));Text(p.label,color=LumyrinthColor.TextPrimary,fontSize=12.sp,modifier=Modifier.weight(1f));Text("$s sec",color=LumyrinthColor.TextSecondary,fontSize=11.sp);Icon(Icons.Rounded.ChevronRight,null,tint=LumyrinthColor.TextTertiary,modifier=Modifier.size(15.dp))}
@Composable private fun SettingLine(i:ImageVector,t:String,v:String)=Row(Modifier.fillMaxWidth().height(40.dp),verticalAlignment=Alignment.CenterVertically){Icon(i,null,tint=LumyrinthColor.VioletBright,modifier=Modifier.size(18.dp));Spacer(Modifier.width(9.dp));Text(t,color=LumyrinthColor.TextPrimary,fontSize=12.sp,modifier=Modifier.weight(1f));Text(v,color=LumyrinthColor.TextSecondary,fontSize=11.sp);Icon(Icons.Rounded.ChevronRight,null,tint=LumyrinthColor.TextTertiary,modifier=Modifier.size(15.dp))}
@Composable private fun Stepper(t:String,v:Int,c:Color,change:(Int)->Unit)=Row(Modifier.fillMaxWidth().height(50.dp).background(Brush.horizontalGradient(listOf(c.copy(.5f),c.copy(.18f))),RoundedCornerShape(14.dp)).border(1.dp,c.copy(.42f),RoundedCornerShape(14.dp)).padding(horizontal=12.dp),verticalAlignment=Alignment.CenterVertically){Text(t,color=LumyrinthColor.TextPrimary,fontWeight=FontWeight.Medium,modifier=Modifier.weight(1f));Step(Icons.Rounded.Remove){change(v-1)};Text(v.toString(),color=LumyrinthColor.TextPrimary,fontSize=22.sp,modifier=Modifier.width(36.dp),textAlign=TextAlign.Center);Step(Icons.Rounded.Add){change(v+1)}}
@Composable private fun Step(i:ImageVector,click:()->Unit)=IconButton(click,Modifier.size(29.dp).background(Color.Black.copy(.24f),CircleShape)){Icon(i,null,tint=LumyrinthColor.TextPrimary,modifier=Modifier.size(16.dp))}
@Composable private fun ToggleRow(t:String,sub:String,i:ImageVector,on:Boolean,change:(Boolean)->Unit)=Row(Modifier.fillMaxWidth().background(LumyrinthColor.Surface,RoundedCornerShape(17.dp)).border(1.dp,LumyrinthColor.Border,RoundedCornerShape(17.dp)).padding(14.dp),verticalAlignment=Alignment.CenterVertically){Icon(i,null,tint=LumyrinthColor.VioletBright,modifier=Modifier.size(21.dp));Spacer(Modifier.width(11.dp));Column(Modifier.weight(1f)){Text(t,color=LumyrinthColor.TextPrimary,fontWeight=FontWeight.Medium);Text(sub,color=LumyrinthColor.TextSecondary,fontSize=10.sp)};Switch(on,change,colors=SwitchDefaults.colors(checkedTrackColor=LumyrinthColor.Violet,checkedThumbColor=Color.White))}

@Composable private fun SmallMetric(t:String,v:String,modifier:Modifier=Modifier)=Column(modifier,horizontalAlignment=Alignment.CenterHorizontally){Text(v,color=LumyrinthColor.TextPrimary,fontSize=13.sp,fontWeight=FontWeight.SemiBold);Text(t,color=LumyrinthColor.TextTertiary,fontSize=8.sp)}
@Composable private fun Metric(v:String,t:String,suffix:String="")=Column(horizontalAlignment=Alignment.CenterHorizontally){Row(verticalAlignment=Alignment.Bottom){Text(v,color=LumyrinthColor.TextPrimary,fontSize=26.sp);if(suffix.isNotEmpty())Text(" $suffix",color=LumyrinthColor.TextSecondary,fontSize=11.sp,modifier=Modifier.padding(bottom=4.dp))};Text(t,color=LumyrinthColor.TextSecondary,fontSize=10.sp)}
@Composable private fun MoodChoice(t:String,i:ImageVector,on:Boolean,click:()->Unit)=Column(Modifier.clip(RoundedCornerShape(13.dp)).clickable(onClick=click).background(if(on)LumyrinthColor.Violet.copy(.24f)else Color.Transparent).padding(8.dp),horizontalAlignment=Alignment.CenterHorizontally){Icon(i,null,tint=if(t=="Not great")LumyrinthColor.Orange else LumyrinthColor.VioletBright,modifier=Modifier.size(29.dp));Text(t,color=LumyrinthColor.TextSecondary,fontSize=9.sp)}

@Composable private fun WeekChart(values:List<Int>){val max=(values.maxOrNull()?:1).coerceAtLeast(1);Row(Modifier.fillMaxWidth().height(145.dp).background(LumyrinthColor.Surface,RoundedCornerShape(19.dp)).border(1.dp,LumyrinthColor.Border,RoundedCornerShape(19.dp)).padding(15.dp),horizontalArrangement=Arrangement.SpaceAround,verticalAlignment=Alignment.Bottom){values.forEachIndexed{i,v->Column(Modifier.fillMaxHeight(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Bottom){Box(Modifier.width(15.dp).weight(1f),contentAlignment=Alignment.BottomCenter){Box(Modifier.fillMaxWidth().fillMaxHeight((v.toFloat()/max).coerceAtLeast(.08f)).background(Brush.verticalGradient(listOf(LumyrinthColor.VioletBright,if(i>=5)LumyrinthColor.Orange else LumyrinthColor.DeepViolet)),RoundedCornerShape(4.dp)))};Spacer(Modifier.height(7.dp));Text(listOf("M","T","W","T","F","S","S")[i],color=LumyrinthColor.TextSecondary,fontSize=9.sp)}}}}
@Composable private fun Calendar(sessions:List<SessionEntity>){val today=LocalDate.now();val first=today.withDayOfMonth(1);val days=sessions.filter{it.completed}.map{Instant.ofEpochMilli(it.startedAtEpochMillis).atZone(ZoneId.systemDefault()).toLocalDate()}.toSet();val offset=first.dayOfWeek.value-1;val cells=List<LocalDate?>(offset){null}+(1..first.lengthOfMonth()).map{first.withDayOfMonth(it)};Column(Modifier.fillMaxWidth().background(LumyrinthColor.Surface,RoundedCornerShape(19.dp)).border(1.dp,LumyrinthColor.Border,RoundedCornerShape(19.dp)).padding(13.dp)){Row{listOf("M","T","W","T","F","S","S").forEach{Text(it,color=LumyrinthColor.TextTertiary,fontSize=9.sp,textAlign=TextAlign.Center,modifier=Modifier.weight(1f))}};cells.chunked(7).forEach{week->Row(Modifier.fillMaxWidth()){repeat(7){i->val d=week.getOrNull(i);Box(Modifier.weight(1f).aspectRatio(1f),contentAlignment=Alignment.Center){if(d!=null)Text(d.dayOfMonth.toString(),color=if(d in days)LumyrinthColor.TextPrimary else LumyrinthColor.TextSecondary,fontSize=10.sp,modifier=Modifier.background(if(d in days)LumyrinthColor.Violet.copy(.38f)else Color.Transparent,CircleShape).padding(6.dp))}}}}}}

@Composable private fun Core(p:Float,phase:BreathPhase,size:Dp){val breathing=when(phase){BreathPhase.INHALE->.72f+.28f*p;BreathPhase.EXHALE->1f-.28f*p;else->1f};val loop=rememberInfiniteTransition(label="core");val pulse by loop.animateFloat(1f,1.045f,infiniteRepeatable(tween(1050,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="pulse");val scale=breathing*if(phase.name.startsWith("HOLD"))pulse else 1f;Canvas(Modifier.size(size)){val r=this.size.minDimension*.42f*scale;drawCircle(Brush.radialGradient(listOf(LumyrinthColor.Magenta.copy(.27f),LumyrinthColor.Violet.copy(.09f),Color.Transparent),center,r*1.38f),r*1.38f);repeat(8){i->val rr=r*(.2f+i*.1f);val c=when(i%3){0->LumyrinthColor.Magenta;1->LumyrinthColor.VioletBright;else->LumyrinthColor.Blue};val start=i*43f+p*16f;drawArc(c.copy(alpha=.84f-i*.055f),start,328f-i*2f,false,center-Offset(rr,rr),Size(rr*2,rr*2),style=Stroke(if(i<2)3.4f else 2f));val rad=Math.toRadians(start.toDouble());drawCircle(c.copy(.85f),2.1f+i*.12f,center+Offset(rr*cos(rad).toFloat(),rr*sin(rad).toFloat()))};drawCircle(LumyrinthColor.Orange.copy(.22f),r*.16f);drawCircle(Color.White,r*.052f)}}
@Composable private fun MiniCore(size:Dp)=Canvas(Modifier.size(size)){val r=this.size.minDimension*.42f;repeat(4){i->val rr=r*(.35f+i*.2f);drawArc(if(i%2==0)LumyrinthColor.VioletBright else LumyrinthColor.Magenta,i*47f,310f,false,center-Offset(rr,rr),Size(rr*2,rr*2),style=Stroke(1.8f))};drawCircle(Color.White,2.2f)}
@Composable private fun Wave(modifier:Modifier)=Canvas(modifier){val path=Path();for(x in 0..size.width.toInt()){val y=size.height/2+sin(x/19f)*size.height*.28f;if(x==0)path.moveTo(x.toFloat(),y)else path.lineTo(x.toFloat(),y)};drawPath(path,Brush.horizontalGradient(listOf(LumyrinthColor.Orange,LumyrinthColor.Magenta,LumyrinthColor.VioletBright)),style=Stroke(2.1f))}
@Composable private fun Dotted(modifier:Modifier)=Canvas(modifier){for(x in 0..size.width.toInt() step 8){val y=size.height/2+sin(x/21f)*size.height*.25f;drawCircle(LumyrinthColor.VioletBright.copy(.8f),1.4f,Offset(x.toFloat(),y))}}

private fun phaseColor(p:BreathPhase)=when(p){BreathPhase.INHALE->LumyrinthColor.VioletBright;BreathPhase.HOLD_AFTER_INHALE->LumyrinthColor.Magenta;BreathPhase.EXHALE->LumyrinthColor.Orange;BreathPhase.HOLD_AFTER_EXHALE->Color(0xFFE0B04B)}
private fun patternText(p:BreathingPattern)=p.phases().joinToString(" · "){(phase,s)->"${phase.label} $s"}
private fun weekMinutes(s:List<SessionEntity>):List<Int>{val zone=ZoneId.systemDefault();val start=LocalDate.now(zone).with(DayOfWeek.MONDAY);return(0L..6L).map{o->val d=start.plusDays(o);(s.filter{Instant.ofEpochMilli(it.startedAtEpochMillis).atZone(zone).toLocalDate()==d}.sumOf{it.actualDurationMillis}/60000).toInt()}}
private val ExerciseCategory.title:String get()=name.lowercase().replaceFirstChar{it.uppercase()}
private fun greeting()=when(LocalTime.now().hour){in 5..11->"Good morning";in 12..16->"Good afternoon";else->"Good evening"}
