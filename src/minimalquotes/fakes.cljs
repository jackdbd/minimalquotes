(ns minimalquotes.fakes)

(def user-id "q2MbgShXTQgIbKipyGGKK0ZC7Yv2")

(def user {:display-name "John Doe"
           :email "john.doe@emailprovider.com"
           :photo-url "img/minimalquotes-icon.png"
           :uid user-id})

(def tag-art {:color "indigo"
              :description "A tag about art"
              :name "art"})
(def tag-cool {:color "purple"
               :description "A tag about cool stuff"
               :name "cool"})
(def tag-friendship {:color "yellow"
                     :description "A tag about friendship"
                     :name "friendship"})
(def tag-love {:color "red"
               :description "A tag about love"
               :name "love"})
(def tag-money {:color "green"
                :description "A tag about money"
                :name "money"})
(def tag-motivation {:color "pink"
                     :description "A tag about motivation"
                     :name "motivation"})
(def tag-skill {:color "teal"
                :description "A tag about skill"
                :name "skill"})
(def tag-style {:color "gray"
                :description "A tag about style"
                :name "style"})
(def tag-wisdom {:color "orange"
                 :description "A tag about wisdom"
                 :name "wisdom"})

(def tags {:art tag-art
           :cool tag-cool
           :friendship tag-friendship
           :love tag-love
           :money tag-money
           :motivation tag-motivation
           :skill tag-skill
           :style tag-style
           :wisdom tag-wisdom})

(def quote-id-0 "Hvx2oAFAQ3GWKHUcqRdu")
(def quote-id-1 "ftnfLpbqkBKdWegivYZa")
(def quote-id-2 "MUORu1tzEnqZ75jUUkB6")
(def quote-id-3 "j2zYfWIJ2Yds6pnMUfvo")
(def quote-id-4 "lhhGqYQrNsVb0ZyrV1NW")
(def quote-id-5 "tTZSa7xGoY61lePYs2YX")

(def author-0 "Jeff Atwood")
(def text-0 "You can never have too little minimalism.")

(def quote-0 {:author author-0
              :tags tags
              :text text-0})

(def quotes {quote-id-0 quote-0
             quote-id-1 {:author "Mark Twain"
                         :tags (select-keys tags [:love :money])
                         :text "Too much of anything is bad, but too much whiskey is just enough."}
             quote-id-2 {:author "Nicolò Machiavelli"
                         :tags (select-keys tags [:style :wisdom])
                         :text "Both fortune and virtue are needed to become a prince. Without virtue, no fortune can last; without fortune, your virtues may be useless."}
             quote-id-3 {:author "Zen master"
                         :tags (select-keys tags [:motivation :wisdom])
                         :text "If you walk, just walk. If you sit, just sit; but whatever you do, don't wobble."}
             quote-id-4 {:author "Leon Battista Alberti"
                         :tags (select-keys tags [:art :motivation :skill :wisdom])
                         :text "No art, however minor, demands less than total dedication if you want to excel in it."}
             quote-id-5 {:author "It doesn’t matter how beautiful your theory is, it doesn’t matter how smart you are. If it doesn’t agree with experiment, it’s wrong."
                         :tags tags
                         :text "Richard Feynman"}})
