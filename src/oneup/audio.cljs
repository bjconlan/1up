(ns oneup.audio
  (:require [sablono.core :refer-macros [html]]
            [om.next :as om :refer-macros [defui]]))

(def {0 {:note :a :filter :square :volume 0.5}
      4 {:note :b :filter :saw :volume 0.5}})