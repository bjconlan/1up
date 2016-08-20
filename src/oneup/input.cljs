(ns oneup.input)

(def state
  {:keyboard #{}
   :gamepad  #{}
   :mouse    {:x nil :y nil}})

(defn register-keyboard-listeners [el]
  (doto el
    (.addEventListener "keydown" (fn [e] (do
                                           (update state :keyboard #(conj % (.-keycode e)))
                                           (.preventDefault e))), true)
    (.addEventListener "keyup" (fn [e] (do
                                         (update state :keyboard #(disj % (.-keycode e)))
                                         (.preventDefault e))), true)))