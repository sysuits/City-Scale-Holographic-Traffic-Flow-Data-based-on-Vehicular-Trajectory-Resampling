# City-Scale Holographic Traffic Flow Data based on Vehicular Trajectory Resampling


##  Introduction

Despite abundant accessible traffic data, researches on traffic flow estimation and optimization still face the dilemma of detailedness and integrity in the measurement. A dataset of city-scale vehicular continuous trajectories featuring the finest resolution and integrity, as known as the holographic traffic data, would be a breakthrough, for it could reproduce every detail of the traffic flow evolution and reveal the personal mobility pattern within the city. Due to the high coverage of Automatic Vehicle Identification (AVI) devices in Xuancheng city, we constructed one-month continuous trajectories of daily 80,000 vehicles in the city with accurate intersection passing time and no travel path estimation bias. With such holographic traffic data, it is possible to reproduce every detail of the traffic flow evolution. We presented a set of traffic flow data based on the holographic trajectories resampling, covering the whole 482 road segments in the city round the clock, including stationary average speed and flow data of 5-minute intervals and dynamic floating car data.

**This repository is the data processing code related to the [research paper](https://arxiv.org/abs/2108.13376).**

**To access the resampled traffic data, please find in our open data platform [VSensor](https://vsensor.openits.cn/#/about).**


## File Structure

In this repository, we provide code and instructions for reproducing the presented results in the paper. In general, files that end with ".py" are supporting python module files, while other files with ".ipynb" are written as Jupyter Notebook instruction.  The instruction files demonstrate the whole data processing workflow in the following work flow, including trip measurement, trajectory reconstruction, virtual traffic flow detection, and data validation.
These files can be used to better understand the modeling and validation steps.

![workflow](https://github.com/sysuits/City-Scale-Holographic-Traffic-Flow-Data-based-on-Vehicular-Trajectory-Resampling/blob/master/img/workflow.png)








   













