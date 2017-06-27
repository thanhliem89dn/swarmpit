(ns swarmpit.component.service.form-ports
  (:require [material.icon :as icon]
            [material.component :as comp]
            [swarmpit.component.state :as state]
            [rum.core :as rum]))

(enable-console-print!)

(def cursor [:page :service :wizard :ports])

(def headers [{:name  "Container port"
               :width "30%"}
              {:name  "Protocol"
               :width "30%"}
              {:name  "Host port"
               :width "30%"}])

(def empty-info
  (comp/form-value "Service has no published ports."))

(defn- format-port-value
  [value]
  (if (zero? value) "" value))

(defn- form-container [value index]
  (comp/form-list-textfield
    {:name     (str "form-container-text-" index)
     :key      (str "form-container-text-" index)
     :type     "number"
     :min      1
     :max      65535
     :value    (format-port-value value)
     :onChange (fn [_ v]
                 (state/update-item index :containerPort (js/parseInt v) cursor))}))

(defn- form-protocol [value index]
  (comp/form-list-selectfield
    {:name     (str "form-protocol-select-" index)
     :key      (str "form-protocol-select-" index)
     :value    value
     :onChange (fn [_ _ v]
                 (state/update-item index :protocol v cursor))}
    (comp/menu-item
      {:name        (str "form-protocol-tcp-" index)
       :key         (str "form-protocol-tcp-" index)
       :value       "tcp"
       :primaryText "TCP"})
    (comp/menu-item
      {:name        (str "form-protocol-udp-" index)
       :key         (str "form-protocol-udp-" index)
       :value       "udp"
       :primaryText "UDP"})))

(defn- form-host [value index]
  (comp/form-list-textfield
    {:name     (str "form-host-text-" index)
     :key      (str "form-host-text-" index)
     :type     "number"
     :min      1
     :max      65535
     :value    (format-port-value value)
     :onChange (fn [_ v]
                 (state/update-item index :hostPort (js/parseInt v) cursor))}))

(defn- render-ports
  [item index _]
  (let [{:keys [containerPort
                protocol
                hostPort]} item]
    [{:item  (form-container containerPort index)
      :width "30%"}
     {:item  (form-protocol protocol index)
      :width "30%"}
     {:item  (form-host hostPort index)
      :width "30%"}]))

(defn- form-table
  [ports]
  (comp/form-table headers
                   ports
                   nil
                   render-ports
                   (fn [index] (state/remove-item index cursor))))

(defn- add-item
  []
  (state/add-item {:containerPort 0
                   :protocol      "tcp"
                   :hostPort      0} cursor))

(def render-item-keys
  [[:containerPort] [:protocol] [:hostPort]])

(defn- render-item
  [item]
  (val item))

(rum/defc form-create < rum/reactive []
  (let [ports (state/react cursor)]
    [:div
     (comp/form-add-btn "Publish port" add-item)
     (if (not (empty? ports))
       (form-table ports))]))

(rum/defc form-update < rum/reactive []
  (let [ports (state/react cursor)]
    (if (empty? ports)
      empty-info
      (form-table ports))))

(rum/defc form-view < rum/static [ports]
  (if (empty? ports)
    empty-info
    (comp/form-info-table headers
                          ports
                          render-item
                          render-item-keys
                          "300px")))