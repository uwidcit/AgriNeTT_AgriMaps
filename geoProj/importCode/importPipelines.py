from django.contrib.gis.utils import LayerMapping
from models import Pipelines

pipelinesJsonFile = '/Users/kiran/Downloads/pipelines1.json'
mapping = {
	'objectID':'OBJECTID',
	'pipelineID':'PIPELIN_ID',
	'size':'SIZE_',
	'datasource':'DATASOURCE',
	'useID':'USE_ID',
	'pipe_type':'PIPE_TYPE',
	'predecess':'PREDECESSO',
	'status':'STATUS',
	'level':'LEVEL_',
	'shape_length':'SHAPE_Leng',
	'ownerID':'OWNER_ID',
	'geomData':'GEOMETRY'
}

lm = LayerMapping(Pipelines,pipelinesJsonFile,mapping)
lm.save(verbose=True)