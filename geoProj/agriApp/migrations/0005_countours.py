# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import django.contrib.gis.db.models.fields


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0004_pipelines'),
    ]

    operations = [
        migrations.CreateModel(
            name='Countours',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('trt', models.IntegerField()),
                ('trt_i', models.IntegerField()),
                ('height', models.IntegerField()),
                ('htm', models.FloatField()),
                ('length', models.FloatField()),
                ('rpoly', models.IntegerField()),
                ('lpoly', models.IntegerField()),
                ('tnode', models.IntegerField()),
                ('fnode', models.IntegerField()),
                ('geomData', django.contrib.gis.db.models.fields.MultiLineStringField(srid=4326)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
    ]
