/*
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

package ch.deletescape.lawnchair.globalsearch.providers

import android.content.*
import android.graphics.drawable.Drawable
import androidx.annotation.Keep
import ch.deletescape.lawnchair.colors.ColorEngine
import ch.deletescape.lawnchair.globalsearch.SearchProvider
import com.android.launcher3.R
import com.android.launcher3.util.PackageManagerHelper

@Keep
class SFinderSearchProvider(context: Context) : SearchProvider(context) {

    override val name: String = context.getString(R.string.search_provider_s_finder)
    override val supportsVoiceSearch: Boolean
        get() = true
    // TODO: Support bixby
    override val supportsAssistant: Boolean
        get() = false
    override val supportsFeed = false

    override val isAvailable: Boolean
        get() = PackageManagerHelper.isAppEnabled(context.packageManager, PACKAGE, 0)

    override fun startSearch(callback: (intent: Intent) -> Unit) {
        callback(Intent(Intent.ACTION_MAIN)
                .setClassName(PACKAGE, CLASS))
    }

    override fun startVoiceSearch(callback: (intent: Intent) -> Unit) {
        callback(Intent(Intent.ACTION_MAIN)
                .setClassName(PACKAGE, CLASS)
                .putExtra("launch_mode", "voice_input"))
    }

    override fun getIcon(): Drawable = context.getDrawable(R.drawable.ic_search)!!.mutate().apply {
        setTint(ColorEngine.getInstance(context).accent)
    }

    override fun getVoiceIcon(): Drawable = context.getDrawable(R.drawable.ic_mic_color)!!.mutate().apply {
        setTint(ColorEngine.getInstance(context).accent)
    }

    companion object {
        const val PACKAGE = "com.samsung.android.app.galaxyfinder"
        const val CLASS = "$PACKAGE.GalaxyFinderActivity"
    }
}
