; rename to μtiny
(ns oneup.core
  (:require [clj-di.core :as di :include-macros true]
            [goog.webgl :as webgl]))

(def code-page-437 [])

(defn compile-shader [src type]
  (let [gl (di/get-dep :gl)
        shader (.createShader gl type)]
    (do (.shaderSource gl shader src)
        (.compileShader gl shader)
        (if (.getShaderParameter gl shader webgl/COMPILE_STATUS)
          shader
          (js/console.log "Failed to compile shader:" (.getShaderInfoLog gl shader))))))

(defn create-program [vs fs]
  (let [gl (di/get-dep :gl)
        program (.createProgram gl)]
    (do (.attachShader gl program vs)
        (.attachShader gl program fs)
        (.linkProgram gl program)
        (if (.getProgramParameter gl program webgl/LINK_STATUS)
          program
          (js/console.log "Failed to link program:" (.getProgramInfoLog gl program))))))

(def vertexShaderSrc "
attribute vec2 a_position;

void main () {
  gl_Position = vec4(a_position, 0, 1);
}")


(def fragmentShaderSrc "
precision mediump float;

uniform vec2  u_screenResolution;
uniform float u_time;

void mainImage(in vec4 fragCoord, out vec4 fragColor) {
  vec2 uv = fragCoord.xy / u_screenResolution;
  fragColor = vec4(uv, 0.5 + 0.5 * sin(u_time), 1.0);
}

void main() {
  mainImage(gl_FragCoord, gl_FragColor);
}")

(defn app []
  (let [gl (di/get-dep :gl)
        width (.-width (.-canvas gl))
        height (.-height (.-canvas gl))
        program (create-program (compile-shader vertexShaderSrc webgl/VERTEX_SHADER)
                                (compile-shader fragmentShaderSrc webgl/FRAGMENT_SHADER))
        uniforms {:screenResolution (.getUniformLocation gl program "u_screenResolution")
                  :time             (.getUniformLocation gl program "u_time")}
        pos-loc (.getAttribLocation gl program "a_position")]
    (do
      (.bindBuffer gl webgl/ARRAY_BUFFER (.createBuffer gl))
      (.bufferData gl webgl/ARRAY_BUFFER (js/Float32Array. [-1.0, -1.0, 1.0, -1.0, -1.0, 1.0, -1.0, 1.0, 1.0, -1.0, 1.0, 1.0]) webgl/STATIC_DRAW)
      (.enableVertexAttribArray gl pos-loc)
      (.vertexAttribPointer gl pos-loc 2 webgl/FLOAT false 0 0)
      ;pos-loc
      )
    (letfn [(render [time]
              (do
                (.viewport gl 0 0 width height)
                (.enable gl webgl/DEPTH_TEST)
                (.enable gl webgl/CULL_FACE)
                (.clear gl (bit-or webgl/COLOR_BUFFER_BIT webgl/DEPTH_BUFFER_BIT))

                (.useProgram gl program)
                (.uniform2f gl (:screenResolution uniforms) width height)
                (.uniform1f gl (:time uniforms) (* time 0.001))
                (.drawArrays gl webgl/TRIANGLES 0 6)
                (js/requestAnimationFrame render)))]
      (js/requestAnimationFrame render))))

; Review, managers: buffer manager, texture manager, shader manager
(defn ^:export bootstrap [el]
  (let [gl (doto
             (or (.getContext el "webgl") (.getContext el "experimental-webgl"))
             (.getExtension "OES_texture_half_float"))
        ]
    (do
      (bind-input-handlers))
    (di/register! :gl gl
                  :input inputState)
    (app)))