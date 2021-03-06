/*
 * Copyright (C) 2019 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pratham.foundation.customView.submarine_view

import android.animation.Animator
import android.view.ViewPropertyAnimator

/** do something block codes after finish animation. */
internal fun ViewPropertyAnimator.doAfterAnimate(block: () -> Unit) {
  this.setListener(object : Animator.AnimatorListener {
    override fun onAnimationEnd(p0: Animator?) = block()
    override fun onAnimationRepeat(p0: Animator?) = Unit
    override fun onAnimationCancel(p0: Animator?) = Unit
    override fun onAnimationStart(p0: Animator?) = Unit
  })
}
