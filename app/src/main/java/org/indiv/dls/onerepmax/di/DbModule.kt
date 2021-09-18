package org.indiv.dls.onerepmax.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.indiv.dls.onerepmax.data.db.ExerciseDao
import org.indiv.dls.onerepmax.data.db.ExerciseDatabase
import org.indiv.dls.onerepmax.data.db.ExerciseDayDao
import org.indiv.dls.onerepmax.data.db.ExerciseDayEntryDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): ExerciseDatabase {
        return Room.databaseBuilder(
            appContext,
            ExerciseDatabase::class.java,
            "exercise-db"
        ).build()
    }

    @Provides
    fun provideExerciseDao(exerciseDatabase: ExerciseDatabase): ExerciseDao {
        return exerciseDatabase.exerciseDao()
    }

    @Provides
    fun provideExerciseDayDao(exerciseDatabase: ExerciseDatabase): ExerciseDayDao {
        return exerciseDatabase.exerciseDayDao()
    }

    @Provides
    fun provideExerciseDayEntryDao(exerciseDatabase: ExerciseDatabase): ExerciseDayEntryDao {
        return exerciseDatabase.exerciseDayEntryDao()
    }
}
