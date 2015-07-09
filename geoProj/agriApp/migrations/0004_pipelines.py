# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import django.contrib.gis.db.models.fields


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0003_rivers'),
    ]

    operations = [
        migrations.CreateModel(
            name='Pipelines',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('objectID', models.IntegerField()),
                ('pipelineID', models.IntegerField()),
                ('size', models.IntegerField()),
                ('datasource', models.IntegerField()),
                ('useID', models.IntegerField()),
                ('pipe_type', models.IntegerField()),
                ('status', models.IntegerField()),
                ('predecess', models.IntegerField()),
                ('level', models.IntegerField()),
                ('shape_length', models.FloatField()),
                ('ownerID', models.IntegerField()),
                ('geomData', django.contrib.gis.db.models.fields.LineStringField(srid=4326)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
    ]
