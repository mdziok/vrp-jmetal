# vrp-jmetal



library("ggplot2")
setwd("/path/to/vrp-jmetal")
data = read.csv("results.csv")
ggplot() +
  geom_boxplot(data=data,
               aes(x=algorithm_name, y = fitness1, col=base_type)) +
  facet_wrap(~max_evaluations)
